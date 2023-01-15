package uni.project.mylocalbooking.activities.provider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.slider.Slider;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.TimeFramePickerDialogFragment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;


public class ManualBlueprintCreationFragment extends BlueprintCreationFragment {
    private static final int TIME_GRANULARITY_MINUTES = 15;
    private static final String TITLE_TIME = "Time window";
    private static final String TITLE_MAX_DURATION = "Maximum slot duration";
    private ManualSlotBlueprint blueprint;

    private LocalTime fromTime;
    private LocalTime toTime;
    private Slider maxDurationSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout list = view.findViewById(R.id.blueprint_creation_steps_layout);

        addTimeFramePicker(list);
        addMaxDuration(list);

        return view;
    }

    private void addTimeFramePicker(LinearLayout layout) {
        ListView list = new ListView(getContext());
        AvailableBlueprintsAdapter adapter = new AvailableBlueprintsAdapter(
                blueprints.stream().map(b -> (ManualSlotBlueprint) b).collect(Collectors.toList())
        );

        createViewCardView(layout, TITLE_TIME, list, view -> {
            System.out.println(); //TODO: pass the result to the parent activity
        });

        list.setAdapter(adapter);
    }

    private void addMaxDuration(LinearLayout layout) {
        maxDurationSlider = new Slider(requireContext());
        maxDurationSlider.setStepSize(TIME_GRANULARITY_MINUTES);

        createViewCardView(layout, TITLE_MAX_DURATION, maxDurationSlider, view -> {
            System.out.println(); //TODO: pass the result to the parent activity
        });
    }

    protected void onAddBlueprint(ITimeFrame timeFrame) {
        TimeFramePickerDialogFragment slotCreationDialog = new TimeFramePickerDialogFragment(timeFrame, newTimeFrame -> {
            fromTime = newTimeFrame.getStart();
            toTime = newTimeFrame.getEnd();

            maxDurationSlider.setValueFrom(0);
            maxDurationSlider.setValueTo(fromTime.until(toTime, ChronoUnit.MINUTES));
        });
        slotCreationDialog.show(getChildFragmentManager(), "AddBlueprintDialog");
    }
}