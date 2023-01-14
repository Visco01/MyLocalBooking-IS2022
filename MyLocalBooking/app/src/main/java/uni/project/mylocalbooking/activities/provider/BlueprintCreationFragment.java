package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.CollapsibleCardViewFragment;
import uni.project.mylocalbooking.fragments.WeekdayPickerFragment;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class BlueprintCreationFragment extends Fragment implements CollapsibleCardViewFragment.IOnAttachedListener {
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

    protected static final String TITLE_WEEKDAYS = "weekdays";
    protected static final String TITLE_FROMDATE = "fromdate";
    protected static final String TITLE_TODATE = "todate";
    protected static final String TITLE_RESERVATIONS_LIMIT = "reservationlimit";

    protected Collection<SlotBlueprint> blueprints;
    protected Collection<SlotBlueprint> conflictingBlueprints;
    protected final HashMap<String, CardViewInfo> createdCardViews = new HashMap<>();
    protected LocalDate fromDate;
    protected LocalDate toDate;
    protected Integer reservationLimit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blueprints = Arrays.stream(getArguments().getParcelableArray("blueprints"))
                .map(b -> (SlotBlueprint) b)
                .collect(Collectors.toList());
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
            HashSet<DayOfWeek> weekDays = ((WeekdayPickerFragment) getChildFragmentManager().findFragmentByTag(TITLE_WEEKDAYS)).getSelectedDaysOfWeek();

            conflictingBlueprints = blueprints.stream().filter(blueprint -> {

                HashSet<DayOfWeek> intersection = new HashSet<>(weekDays);
                intersection.retainAll(blueprint.weekdays);
                return !intersection.isEmpty();

            }).collect(Collectors.toList());
        });
    }

    private void addFromDate(LinearLayout list) {
        CalendarView calendar = new CalendarView(requireContext());
        calendar.setDate(LocalDate.now().toEpochDay());
        creteViewCardView(list, TITLE_FROMDATE, calendar, view -> {
            fromDate = LocalDate.ofEpochDay(calendar.getDate());
        });
    }

    private void addToDate(LinearLayout list) {
        CalendarView calendar = new CalendarView(requireContext());
        calendar.setDate(LocalDate.now().toEpochDay());
        creteViewCardView(list, TITLE_TODATE, calendar, view -> {
            toDate = LocalDate.ofEpochDay(calendar.getDate());
        });
    }

    private void addReservationLimit(LinearLayout list) {
        EditText limitText = new EditText(requireContext());
        limitText.setInputType(InputType.TYPE_CLASS_NUMBER);

        // TODO: validate input

        creteViewCardView(list, TITLE_RESERVATIONS_LIMIT, limitText, view -> {
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

    private <T extends Fragment> void createFragmentCardView(LinearLayout list, String title, Bundle innerBundle, Class<T> fragmentClass, View.OnClickListener onNext) {
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

    private <T extends View> void creteViewCardView(LinearLayout list, String title, T view, View.OnClickListener onNext) {
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
}