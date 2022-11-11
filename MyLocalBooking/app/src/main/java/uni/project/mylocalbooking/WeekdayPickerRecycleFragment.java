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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeekdayPickerRecycleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekdayPickerRecycleFragment extends Fragment {
    private SlotListViewModel viewModel;

    public WeekdayPickerRecycleFragment() {
        // Required empty public constructor
    }

    public static WeekdayPickerRecycleFragment newInstance() {
        return new WeekdayPickerRecycleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekday_picker_recycle, container, false);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        viewModel = new ViewModelProvider(requireActivity()).get(SlotListViewModel.class);
        recyclerView.setAdapter(new WeekdayPickerAdapter(viewModel));

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        return view;
    }
}