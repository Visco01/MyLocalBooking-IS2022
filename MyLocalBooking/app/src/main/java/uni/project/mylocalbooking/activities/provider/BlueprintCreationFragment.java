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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    protected abstract class CardViewInfo {
        protected final String title;
        protected final View.OnClickListener listener;
        protected final CollapsibleCardViewFragment cardViewFragment;

        private CardViewInfo(String title, CollapsibleCardViewFragment cardViewFragment, View.OnClickListener listener) {
            this.title = title;
            this.listener = listener;
            this.cardViewFragment = cardViewFragment;

            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(cardViewFragment, title)
                    .commit();
        }

        public void collapse() {
            cardViewFragment.collapse();
        }

        public void expand() {
            cardViewFragment.expand();
        }

        protected abstract void create();
    }
    private class FragmentCardViewInfo<T extends Fragment> extends CardViewInfo {
        private final Class<T> innerFragmentType;
        public T innerFragment;
        private final Bundle bundle;

        private FragmentCardViewInfo(String title, CollapsibleCardViewFragment cardViewFragment, Class<T> innerFragmentType, Bundle bundle, View.OnClickListener listener) {
            super(title, cardViewFragment, listener);
            this.innerFragmentType = innerFragmentType;
            this.bundle = bundle;
        }

        protected void create() {
            innerFragment = cardViewFragment.setContent(innerFragmentType, bundle, listener);
        }
    }
    private class ViewCardViewInfo<T extends View> extends CardViewInfo {
        private final T view;

        private ViewCardViewInfo(String title, CollapsibleCardViewFragment cardViewFragment, T view, View.OnClickListener listener) {
            super(title, cardViewFragment, listener);
            this.view = view;
        }

        protected void create() {
            cardViewFragment.setContent(view, listener);
        }
    }

    private class BlueprintCreationStepsAdapter extends BaseAdapter {
        private final List<BlueprintCreationFragment.CardViewInfo> cardViewInfo;
        private final HashMap<CollapsibleCardViewFragment, View> attachedFragments = new HashMap();
        public BlueprintCreationStepsAdapter(List<BlueprintCreationFragment.CardViewInfo> cardViewInfo) {
            this.cardViewInfo = cardViewInfo;
        }

        @Override
        public int getCount() {
            return cardViewInfo.size();
        }

        @Override
        public Object getItem(int i) {
            return cardViewInfo.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            BlueprintCreationFragment.CardViewInfo info = cardViewInfo.get(i);

            if(info.cardViewFragment.isDetached()) {
                FragmentContainerView fragmentContainer = addFragmentContainer();
                View fragmentView = getChildFragmentManager().findFragmentByTag(info.title).getView();
                fragmentContainer.addView(fragmentView);
                return fragmentContainer;
            }

            return info.cardViewFragment.getView();
        }

        private FragmentContainerView addFragmentContainer() {
            FragmentContainerView fragmentContainer = new FragmentContainerView(requireContext());
            fragmentContainer.setId(View.generateViewId());
            fragmentContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return fragmentContainer;
        }
    }


    protected static final String TITLE_WEEKDAYS = "Weekdays";
    protected static final String TITLE_FROM_DATE = "From date";
    protected static final String TITLE_TO_DATE = "To date";
    protected static final String TITLE_RESERVATIONS_LIMIT = "Reservation limit";

    private int stepCounter = -1;
    private String lastValidStep;
    private HashMap<String, Integer> stepsOrder = new HashMap<>();
    private List<String> stepsList = new ArrayList<>();
    private CardViewInfo lastExpanded;

    protected Establishment establishment;
    protected Collection<SlotBlueprint> blueprints;
    protected Collection<SlotBlueprint> conflictingBlueprints;
    protected final HashMap<String, CardViewInfo> createdCardViews = new HashMap<>();
    private final List<CardViewInfo> cardViewInfoList = new ArrayList<>();
    protected LocalDate fromDate;
    protected LocalDate toDate;
    protected Integer reservationLimit;
    protected HashSet<DayOfWeek> weekDays;

    private BlueprintCreationStepsAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        establishment = getArguments().getParcelable("establishment");
        blueprints = establishment.blueprints;
        addWeekdays();
        addFromDate();
        addToDate();
        addReservationLimit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blueprint_creation, container, false);
        ListView list = view.findViewById(R.id.blueprint_creation_steps_layout);
        adapter = new BlueprintCreationStepsAdapter(cardViewInfoList);
        list.setAdapter(adapter);
        return view;
    }

    private void addWeekdays() {
        Bundle innerBundle = new Bundle();
        innerBundle.putBoolean("simple", true);

        createFragmentCardView(TITLE_WEEKDAYS, innerBundle, WeekdayPickerFragment.class, view -> {
            weekDays = ((WeekdayPickerFragment) getChildFragmentManager().findFragmentByTag(TITLE_WEEKDAYS)).getSelectedDaysOfWeek();
            conflictingBlueprints = blueprints.stream().filter(blueprint -> {

                HashSet<DayOfWeek> intersection = new HashSet<>(weekDays);
                intersection.retainAll(blueprint.weekdays);
                return !intersection.isEmpty();

            }).collect(Collectors.toList());
        });
    }

    private void addFromDate() {
        CalendarView calendar = new CalendarView(requireContext());
        calendar.setDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        createViewCardView(TITLE_FROM_DATE, calendar, view -> {
            fromDate = LocalDate.ofEpochDay(calendar.getDate());
        });
    }

    private void addToDate() {
        CalendarView calendar = new CalendarView(requireContext());
        calendar.setDate(LocalDate.now().toEpochDay());
        createViewCardView(TITLE_TO_DATE, calendar, view -> {
            toDate = LocalDate.ofEpochDay(calendar.getDate());
        });
    }

    private void addReservationLimit() {
        EditText limitText = new EditText(requireContext());
        limitText.setInputType(InputType.TYPE_CLASS_NUMBER);

        // TODO: validate input

        createViewCardView(TITLE_RESERVATIONS_LIMIT, limitText, view -> {
            int value = Integer.valueOf(String.valueOf(limitText.getText()));
            reservationLimit = value <= 0 ? 1 : value;
        });
    }

    private void addStepToOrder(String title, CardViewInfo info) {
        createdCardViews.put(title, info);
        stepsList.add(title);
        stepsOrder.put(title, stepsList.size() - 1);
        cardViewInfoList.add(info);
    }

    private void advance(String title) {
        lastValidStep = title;
        int current = stepsOrder.get(title);
        if(current < stepsList.size() - 1)
            onCardViewClicked(stepsList.get(current + 1));
    }

    protected  <T extends Fragment> void createFragmentCardView(String title, Bundle innerBundle, Class<T> fragmentClass, View.OnClickListener onNext) {
        CollapsibleCardViewFragment fragment = new CollapsibleCardViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);

        addStepToOrder(title, new FragmentCardViewInfo(title, fragment, fragmentClass, innerBundle, v -> {
            advance(title);
            onNext.onClick(v);
        }));
    }

    protected  <T extends View> void createViewCardView(String title, T view, View.OnClickListener onNext) {
        CollapsibleCardViewFragment fragment = new CollapsibleCardViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);

        addStepToOrder(title, new ViewCardViewInfo<T>(title, fragment, view, v -> {
            advance(title);
            onNext.onClick(v);
        }));
    }

    @Override
    public void onFragmentAttached(String title) {
        createdCardViews.get(title).create();
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onCardViewClicked(String title) {
        Integer lastValidOrder = stepsOrder.get(lastValidStep);
        int order = stepsOrder.get(title);

        if(lastValidOrder == null && order == 0) {
            createdCardViews.get(title).expand();
            lastExpanded = createdCardViews.get(title);
        }
        else if(lastValidOrder != null && order <= lastValidOrder + 1) {
            createdCardViews.get(title).expand();
            if (lastExpanded != null)
                lastExpanded.collapse();
            lastExpanded = createdCardViews.get(title);
        }
    }

    protected void end(SlotBlueprint result) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("blueprint", result);
        getParentFragmentManager().setFragmentResult("blueprint", bundle);
    }

    protected abstract void onAddBlueprint(ITimeFrame timeFrame);

    protected void addTimeFramePicker(String title, View.OnClickListener onClick) {
        List<ITimeFrame> availableTimeframes = extractTimeFrames(
                blueprints.stream().map(b -> (ManualSlotBlueprint) b).collect(Collectors.toList())
        );

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setTag("TIMEFRAMES LAYOUT");

        for(int i = 0; i < availableTimeframes.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.manual_blueprint_list_item, layout, false);
            layout.addView(view, i);
            ITimeFrame timeFrame = availableTimeframes.get(i);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((TextView) view.findViewById(R.id.open_time)).setText(timeFrame.getStart().toString());
            ((TextView) view.findViewById(R.id.close_time)).setText(timeFrame.getEnd().toString());
            ((TextView) view.findViewById(R.id.timeframe_title)).setText(R.string.available_time);

            Button button = view.findViewById(R.id.create_slot_button);
            button.setText(R.string.create_blueprint);
            button.setOnClickListener(v -> onAddBlueprint(timeFrame));
        }

        createViewCardView(title, layout, onClick);
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
}