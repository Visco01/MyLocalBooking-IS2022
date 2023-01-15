package uni.project.mylocalbooking.activities.provider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.slider.Slider;

import java.time.Duration;
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

    private LocalTime openTime;
    private LocalTime closeTime;
    private Slider maxDurationSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addTimeFramePicker();
        addMaxDuration();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void addTimeFramePicker() {
        ListView list = new ListView(getContext());
        AvailableBlueprintsAdapter adapter = new AvailableBlueprintsAdapter(
                blueprints.stream().map(b -> (ManualSlotBlueprint) b).collect(Collectors.toList())
        );

        createViewCardView(TITLE_TIME, list, view -> {
            createdCardViews.get(TITLE_MAX_DURATION).expand();
            createdCardViews.get(TITLE_TIME).collapse();
        });

        list.setAdapter(adapter);
    }

    private void addMaxDuration() {
        maxDurationSlider = new Slider(requireContext());
        maxDurationSlider.setStepSize(TIME_GRANULARITY_MINUTES);

        createViewCardView(TITLE_MAX_DURATION, maxDurationSlider, view -> {
            super.end(new ManualSlotBlueprint(
                    openTime,
                    closeTime,
                    Duration.ofMinutes((int) maxDurationSlider.getValue()),
                    establishment,
                    reservationLimit,
                    weekDays,
                    fromDate,
                    toDate
            ));
        });
    }

    protected void onAddBlueprint(ITimeFrame timeFrame) {
        TimeFramePickerDialogFragment slotCreationDialog = new TimeFramePickerDialogFragment(timeFrame, newTimeFrame -> {
            openTime = newTimeFrame.getStart();
            closeTime = newTimeFrame.getEnd();

            maxDurationSlider.setValueFrom(0);
            maxDurationSlider.setValueTo(openTime.until(closeTime, ChronoUnit.MINUTES));
        });
        slotCreationDialog.show(getChildFragmentManager(), "AddBlueprintDialog");
    }
}