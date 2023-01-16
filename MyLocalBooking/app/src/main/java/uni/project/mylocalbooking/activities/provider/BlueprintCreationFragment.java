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
import android.widget.ListView;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.CollapsibleCardViewFragment;
import uni.project.mylocalbooking.fragments.WeekdayPickerFragment;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.SlotBlueprint;

public abstract class BlueprintCreationFragment extends Fragment implements CollapsibleCardViewFragment.ICollapsibleCardViewParent {
    private class BlueprintCreationStepsAdapter extends BaseAdapter {
        private final List<CollapsibleCardViewFragment> cardViewFragments;
        public BlueprintCreationStepsAdapter(List<CollapsibleCardViewFragment> cardViewFragments) {
            this.cardViewFragments = cardViewFragments;
        }

        @Override
        public int getCount() {
            return cardViewFragments.size();
        }

        @Override
        public Object getItem(int i) {
            return cardViewFragments.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            CollapsibleCardViewFragment cardView = cardViewFragments.get(i);

            if(cardView.innerFragment.isDetached()) {
                FragmentContainerView fragmentContainer = addFragmentContainer();
                View fragmentView = getChildFragmentManager().findFragmentByTag(cardView.title).getView();
                fragmentContainer.addView(fragmentView);
                return fragmentContainer;
            }

            return cardView.innerFragment.getView();
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
    private CollapsibleCardViewFragment lastExpanded;

    protected Establishment establishment;
    protected Collection<SlotBlueprint> blueprints;
    protected Collection<SlotBlueprint> conflictingBlueprints;
    protected final HashMap<String, CollapsibleCardViewFragment> createdCardViews = new HashMap<>();
    private final List<CollapsibleCardViewFragment> cardViewInfoList = new ArrayList<>();
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
        WeekdayPickerFragment fragment = new WeekdayPickerFragment();
        Bundle innerBundle = new Bundle();
        innerBundle.putBoolean("simple", true);
        fragment.setArguments(innerBundle);

        createFragmentCardView("WeekDays", fragment, view -> {
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

    private void addStepToOrder(String title, CollapsibleCardViewFragment cardView) {
        createdCardViews.put(title, cardView);
        stepsList.add(title);
        stepsOrder.put(title, stepsList.size() - 1);
        cardViewInfoList.add(cardView);
    }

    private void advance(String title) {
        lastValidStep = title;
        int current = stepsOrder.get(title);
        if(current < stepsList.size() - 1)
            onCardViewClicked(stepsList.get(current + 1));
    }

    protected void createFragmentCardView(String title, Fragment innerFragment, View.OnClickListener onNext) {
        CollapsibleCardViewFragment cardView = new CollapsibleCardViewFragment(title, innerFragment, onNext);

        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.blueprint_creation_steps_layout, cardView, title)
                .commit();

        addStepToOrder(title, cardView);
    }

    protected void createViewCardView(String title, View view, View.OnClickListener onNext) {
        CollapsibleCardViewFragment cardView = new CollapsibleCardViewFragment(title, view, onNext);

        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.blueprint_creation_steps_layout, cardView, title)
                .commit();

        addStepToOrder(title, cardView);
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
                blueprints.stream().map(b -> (ITimeFrame) b).collect(Collectors.toList())
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

    private List<ITimeFrame> extractTimeFrames(Collection<ITimeFrame> blueprints) {
        List<ITimeFrame> results = new ArrayList<>();

        LocalTime previous = LocalTime.MIN;
        for(ITimeFrame blueprint : blueprints) {
            if(previous.compareTo(blueprint.getStart()) < 0) {
                final LocalTime start = previous;
                final LocalTime end = blueprint.getStart();
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
            previous = blueprint.getEnd();
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

        results.sort(Comparator.comparing(ITimeFrame::getStart));
        return results;
    }
}