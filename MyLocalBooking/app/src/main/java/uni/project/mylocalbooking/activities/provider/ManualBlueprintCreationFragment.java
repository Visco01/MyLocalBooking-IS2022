package uni.project.mylocalbooking.activities.provider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.slider.Slider;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import uni.project.mylocalbooking.fragments.TimeFramePickerDialogFragment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;


public class ManualBlueprintCreationFragment extends BlueprintCreationFragment {
    private static final String TITLE_TIME = "Time window";
    private static final String TITLE_MAX_DURATION = "Maximum slot duration";

    private LocalTime openTime;
    private LocalTime closeTime;
    private Slider maxDurationSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addTimeFramePicker(TITLE_TIME, view -> {
            createdCardViews.get(TITLE_MAX_DURATION).expand();
            createdCardViews.get(TITLE_TIME).collapse();
        });
        addMaxDuration();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private void addMaxDuration() {
        maxDurationSlider = new Slider(requireContext());
        maxDurationSlider.setStepSize(TIME_GRANULARITY_MINUTES);

        createViewCardView(TITLE_MAX_DURATION, maxDurationSlider, cardView -> {
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