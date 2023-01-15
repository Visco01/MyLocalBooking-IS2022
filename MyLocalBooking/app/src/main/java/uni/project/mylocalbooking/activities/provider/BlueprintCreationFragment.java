package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.CollapsibleCardViewFragment;
import uni.project.mylocalbooking.fragments.WeekdayPickerFragment;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.SlotBlueprint;

public abstract class BlueprintCreationFragment extends Fragment implements CollapsibleCardViewFragment.IOnAttachedListener {
    private abstract static class CardViewInfo {
        protected final View.OnClickListener listener;
        protected final CollapsibleCardViewFragment cardViewFragment;

        private CardViewInfo(CollapsibleCardViewFragment cardViewFragment, View.OnClickListener listener) {
            this.listener = listener;
            this.cardViewFragment = cardViewFragment;
        }

        protected abstract void create();
    }
    private static class FragmentCardViewInfo<T extends Fragment> extends CardViewInfo {
        private final Class<T> innerFragmentType;
        public T innerFragment;
        private final Bundle bundle;

        private FragmentCardViewInfo(CollapsibleCardViewFragment cardViewFragment, Class<T> innerFragmentType, Bundle bundle, View.OnClickListener listener) {
            super(cardViewFragment, listener);
            this.innerFragmentType = innerFragmentType;
            this.bundle = bundle;
        }

        protected void create() {
            innerFragment = cardViewFragment.setContent(innerFragmentType, bundle, listener);
        }
    }
    private static class ViewCardViewInfo<T extends View> extends CardViewInfo {
        private final T view;

        private ViewCardViewInfo(CollapsibleCardViewFragment cardViewFragment, T view, View.OnClickListener listener) {
            super(cardViewFragment, listener);
            this.view = view;
        }

        protected void create() {
            cardViewFragment.setContent(view, listener);
        }
    }

    protected class AvailableBlueprintsAdapter extends BaseAdapter {
        private final List<ITimeFrame> availableTimeframes;
        protected AvailableBlueprintsAdapter(Collection<ManualSlotBlueprint> blueprints) {
            this.availableTimeframes = extractTimeFrames(blueprints);
        }

        private List<ITimeFrame> extractTimeFrames(Collection<ManualSlotBlueprint> blueprints) {
            List<ITimeFrame> results = new ArrayList<>();

            LocalTime previous = LocalTime.MIN;
            for(ManualSlotBlueprint blueprint : blueprints) {
                if(previous.compareTo(blueprint.openTime) < 0) {
                    final LocalTime start = previous;
                    final LocalTime end = blueprint.openTime;
                    results.add(new ITimeFrame() {
                        @Override
                        public LocalTime getStart() {
                            return start;
                        }

                        @Override
                        public LocalTime getEnd() {
                            return end;
                        }
                    });
                }
                previous = blueprint.closeTime;
            }

            // TODO: right bound should allow 00:00
            LocalTime maxTime = LocalTime.of(23, 59);
            if(previous.compareTo(maxTime) < 0) {
                final LocalTime start = previous;
                results.add(new ITimeFrame() {

                    @Override
                    public LocalTime getStart() {
                        return start;
                    }

                    @Override
                    public LocalTime getEnd() {
                        return maxTime;
                    }
                });
            }
            return results;
        }

        @Override
        public int getCount() {
            return availableTimeframes.size();
        }

        @Override
        public Object getItem(int i) {
            return availableTimeframes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.manual_blueprint_list_item, viewGroup, false);

            ITimeFrame timeFrame = availableTimeframes.get(i);
            ((TextView) view.findViewById(R.id.open_time)).setText(timeFrame.getStart().toString());
            ((TextView) view.findViewById(R.id.close_time)).setText(timeFrame.getEnd().toString());
            ((TextView) view.findViewById(R.id.timeframe_title)).setText(R.string.available_time);

            Button button = view.findViewById(R.id.create_slot_button);
            button.setText(R.string.create_blueprint);
            button.setOnClickListener(v -> onAddBlueprint(timeFrame));

            return view;
        }

    }

    protected static final String TITLE_WEEKDAYS = "Weekdays";
    protected static final String TITLE_FROM_DATE = "From date";
    protected static final String TITLE_TO_DATE = "To date";
    protected static final String TITLE_RESERVATIONS_LIMIT = "Reservation limit";

    protected Establishment establishment;
    protected Collection<SlotBlueprint> blueprints;
    protected Collection<SlotBlueprint> conflictingBlueprints;
    protected final HashMap<String, CardViewInfo> createdCardViews = new HashMap<>();
    protected LocalDate fromDate;
    protected LocalDate toDate;
    protected Integer reservationLimit;
    protected HashSet<DayOfWeek> weekDays;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        establishment = getArguments().getParcelable("establishment");
        blueprints = establishment.blueprints;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blueprint_creation, container, false);
        LinearLayout list = view.findViewById(R.id.blueprint_creation_steps_layout);
        addWeekdays(list);
        addFromDate(list);
        addToDate(list);
        addReservationLimit(list);
        return view;
    }

    private void addWeekdays(LinearLayout list) {
        Bundle innerBundle = new Bundle();
        innerBundle.putBoolean("simple", true);

        createFragmentCardView(list, TITLE_WEEKDAYS, innerBundle, WeekdayPickerFragment.class, view -> {
            weekDays = ((WeekdayPickerFragment) getChildFragmentManager().findFragmentByTag(TITLE_WEEKDAYS)).getSelectedDaysOfWeek();
            conflictingBlueprints = blueprints.stream().filter(blueprint -> {

                HashSet<DayOfWeek> intersection = new HashSet<>(weekDays);
                intersection.retainAll(blueprint.weekdays);
                return !intersection.isEmpty();

            }).collect(Collectors.toList());
        });
    }

    private void addFromDate(LinearLayout list) {
        CalendarView calendar = new CalendarView(requireContext());
        calendar.setDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        createViewCardView(list, TITLE_FROM_DATE, calendar, view -> {
            fromDate = LocalDate.ofEpochDay(calendar.getDate());
        });
    }

    private void addToDate(LinearLayout list) {
        CalendarView calendar = new CalendarView(requireContext());
        calendar.setDate(LocalDate.now().toEpochDay());
        createViewCardView(list, TITLE_TO_DATE, calendar, view -> {
            toDate = LocalDate.ofEpochDay(calendar.getDate());
        });
    }

    private void addReservationLimit(LinearLayout list) {
        EditText limitText = new EditText(requireContext());
        limitText.setInputType(InputType.TYPE_CLASS_NUMBER);

        // TODO: validate input

        createViewCardView(list, TITLE_RESERVATIONS_LIMIT, limitText, view -> {
            int value = Integer.valueOf(String.valueOf(limitText.getText()));
            reservationLimit = value <= 0 ? 1 : value;
        });
    }

    private FragmentContainerView addFragmentContainer(LinearLayout list) {
        FragmentContainerView fragmentContainer = new FragmentContainerView(requireContext());
        fragmentContainer.setId(View.generateViewId());
        list.addView(fragmentContainer);
        return fragmentContainer;
    }

    protected  <T extends Fragment> void createFragmentCardView(LinearLayout list, String title, Bundle innerBundle, Class<T> fragmentClass, View.OnClickListener onNext) {
        CollapsibleCardViewFragment fragment = new CollapsibleCardViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);

        createdCardViews.put(title, new FragmentCardViewInfo(fragment, fragmentClass, innerBundle, v -> {
            fragment.collapse();
            onNext.onClick(v);
        }));

        FragmentContainerView fragmentContainer = addFragmentContainer(list);
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(fragmentContainer.getId(), fragment)
                .commit();
    }

    protected  <T extends View> void createViewCardView(LinearLayout list, String title, T view, View.OnClickListener onNext) {
        CollapsibleCardViewFragment fragment = new CollapsibleCardViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);

        createdCardViews.put(title, new ViewCardViewInfo<T>(fragment, view, v -> {
            fragment.collapse();
            onNext.onClick(v);
        }));

        FragmentContainerView fragmentContainer = addFragmentContainer(list);
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(fragmentContainer.getId(), fragment)
                .commit();
    }

    @Override
    public void notifyFragmentAttached(String title) {
        createdCardViews.get(title).create();
    }

    protected void end(SlotBlueprint result) {
        // TODO: pass result to parent activity
    }

    protected abstract void onAddBlueprint(ITimeFrame timeFrame);
}