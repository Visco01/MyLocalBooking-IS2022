package uni.project.mylocalbooking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activites.client.SlotListViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeekdayPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekdayPickerFragment extends Fragment implements WeekdayPickerAdapter.IWeekdayPickerListener {
    private SlotListViewModel viewModel;

    public WeekdayPickerFragment() {
        // Required empty public constructor
    }

    public static WeekdayPickerFragment newInstance() {
        return new WeekdayPickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SlotListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weekday_picker, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.week_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView.setAdapter(new WeekdayPickerAdapter(this, GetFirstDayOfWeek(LocalDate.now())));
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
                    LocalDate startOfWeek = viewModel.getMinStartOfWeek().getValue().plusWeeks(position);
                    viewModel.setStartOfWeek(startOfWeek);
                }
            }
        });

        return view;
    }

    @Override
    public void onDaySelected(DayOfWeek dow) {
        viewModel.setCurrentDay(dow);
    }

    private LocalDate GetFirstDayOfWeek(LocalDate date) {
        int current_dow = date.getDayOfWeek().getValue();
        int monday_dow = DayOfWeek.MONDAY.getValue();

        if (current_dow > monday_dow)
            return date.minusDays(current_dow - monday_dow);

        return date;
    }
}