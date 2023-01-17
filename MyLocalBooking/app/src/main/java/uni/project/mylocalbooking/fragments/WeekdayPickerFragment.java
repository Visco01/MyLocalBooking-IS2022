package uni.project.mylocalbooking.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.Locale;

import uni.project.mylocalbooking.R;


public class WeekdayPickerFragment extends Fragment implements WeekdayPickerAdapter.IWeekdayPickerListener {
    public static final String MIN_START_OF_WEEK_ARG = "minStartOfWeek";
    public static final String INITIAL_WEEK_ARG = "initialWeek";
    public static final String INITIAL_DAY_ARG = "initialDay";
    public static final String SIMPLE_MODE_ARG = "simple";
    public static final String BACKGROUND_COLOR_ARG = "backgroundColor";
    public static final String ACTIVE_BACKGROUND_COLOR_ARG = "activeBackgroundColor";
    public static final String TEXT_COLOR_ARG = "textColor";
    public static final String ACTIVE_TEXT_COLOR_ARG = "activeTextColor";

    private WeekdayPickerViewModel viewModel;
    private LocalDate minStartOfWeek;
    private LocalDate initialWeek;
    private LocalDate initialDay;
    private boolean simpleWeekdayPicker = true;
    private final HashSet<DayOfWeek> selectedDaysOfWeek = new HashSet<>();
    private int backgroundColor;
    private int activeBackgroundColor;
    private int textColor;
    private int activeTextColor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WeekdayPickerViewModel.class);

        if(savedInstanceState != null)
            return;

        Bundle args = getArguments();
        if(args != null) {
            simpleWeekdayPicker = getArguments().getBoolean(SIMPLE_MODE_ARG);
            if(!simpleWeekdayPicker) {
                minStartOfWeek = args.containsKey(MIN_START_OF_WEEK_ARG) ?
                        getFirstDayOfWeek(LocalDate.ofEpochDay(args.getLong(MIN_START_OF_WEEK_ARG))) :
                        getFirstDayOfWeek(LocalDate.now());
                initialWeek = args.containsKey(INITIAL_WEEK_ARG) ?
                        getFirstDayOfWeek(LocalDate.ofEpochDay(getArguments().getLong(INITIAL_WEEK_ARG))):
                        minStartOfWeek;
                if(args.containsKey(INITIAL_DAY_ARG))
                    initialDay = getFirstDayOfWeek(LocalDate.ofEpochDay(getArguments().getLong(INITIAL_WEEK_ARG)));
                else {
                    LocalDate today = LocalDate.now();
                    initialDay =  today.compareTo(initialWeek) >= 0 ? today : initialWeek;
                }
            }
        }

        backgroundColor = args != null && args.containsKey(BACKGROUND_COLOR_ARG) ?
                args.getInt(BACKGROUND_COLOR_ARG): 0xFF6200EE;

        activeBackgroundColor = args != null && args.containsKey(ACTIVE_BACKGROUND_COLOR_ARG) ?
                args.getInt(ACTIVE_BACKGROUND_COLOR_ARG): 0xFFFFFFFF;

        textColor = args != null && args.containsKey(TEXT_COLOR_ARG) ?
                args.getInt(TEXT_COLOR_ARG): 0xFFFFFFFF;

        activeTextColor = args != null && args.containsKey(ACTIVE_TEXT_COLOR_ARG) ?
                args.getInt(ACTIVE_TEXT_COLOR_ARG): 0xFF000000;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return simpleWeekdayPicker ? initSimple(inflater, container) : init(inflater, container);
    }

    @Override
    public void onDaySelected(DayOfWeek dow) {
        viewModel.setSelectedDayOfWeek(dow);
    }

    public HashSet<DayOfWeek> getSelectedDaysOfWeek() {
        return selectedDaysOfWeek;
    }

    private LocalDate getFirstDayOfWeek(LocalDate date) {
        int current_dow = date.getDayOfWeek().getValue();
        int monday_dow = DayOfWeek.MONDAY.getValue();

        if (current_dow > monday_dow)
            return date.minusDays(current_dow - monday_dow);

        return date;
    }

    private View init(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.weekday_picker, container, false);
        view.findViewById(R.id.weekday_picker_root).setBackgroundColor(backgroundColor);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.week_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView.setAdapter(new WeekdayPickerAdapter(this, initialWeek, initialDay));
        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        TextView month = (TextView) view.findViewById(R.id.weekday_month);
        viewModel.getStartOfWeek().observe(getActivity(), date -> {
            month.setText(date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if(position != -1) {
                    viewModel.setStartOfWeek(minStartOfWeek.plusWeeks(position));
                }
            }
        });

        viewModel.setStartOfWeek(initialWeek);
        viewModel.setSelectedDayOfWeek(LocalDate.now().getDayOfWeek());
        return view;
    }

    private View initSimple(LayoutInflater inflater, ViewGroup container) {
        LinearLayout weekRoot = (LinearLayout) inflater.inflate(R.layout.week, container, false);

        for(int i = 1; i <= 7; i++) {
            View dayView = weekRoot.getChildAt(i - 1);

            final DayOfWeek dow = DayOfWeek.of(i);
            String name = dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            ((TextView) dayView.findViewById(R.id.weekday_name)).setText(name);
            AppCompatButton button = dayView.findViewById(R.id.weekday_button);

            setViewActive(dayView, selectedDaysOfWeek.contains(dow));
            button.setOnClickListener(view -> {
                toggleView(dayView, dow);
                viewModel.toggleSelectedDayOfWeek(dow);
            });
        }
        return weekRoot;
    }

    private void toggleView(View view, DayOfWeek dow) {
        if(selectedDaysOfWeek.contains(dow)) {
            setViewActive(view, false);
            selectedDaysOfWeek.remove(dow);
        } else {
            setViewActive(view, true);
            selectedDaysOfWeek.add(dow);
        }
    }

    private void setViewActive(View view, boolean active) {
        TextView dayText = view.findViewById(R.id.weekday_name);
        TextView numText = view.findViewById(R.id.weekday_number);
        AppCompatButton button = view.findViewById(R.id.weekday_button);

        if(active) {
            button.setBackgroundResource(R.drawable.circle);
            button.setBackgroundTintList(ColorStateList.valueOf(activeBackgroundColor));
            dayText.setTextColor(activeTextColor);
            numText.setTextColor(activeTextColor);
        } else {
            button.setBackgroundColor(backgroundColor);
            dayText.setTextColor(textColor);
            numText.setTextColor(textColor);
        }
    }
}