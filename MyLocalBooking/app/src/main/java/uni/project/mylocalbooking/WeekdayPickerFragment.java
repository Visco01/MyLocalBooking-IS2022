package uni.project.mylocalbooking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeekdayPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekdayPickerFragment extends Fragment {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weekday_picker, container, false);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.week_recycler);
        recyclerView.setLayoutManager(layoutManager);

        viewModel = new ViewModelProvider(requireActivity()).get(SlotListViewModel.class);
        viewModel.setMinStartOfWeek(null);
        recyclerView.setAdapter(new WeekdayPickerAdapter(viewModel));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

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
}