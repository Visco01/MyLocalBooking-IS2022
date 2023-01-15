package uni.project.mylocalbooking.activities.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.slider.RangeSlider;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.TimeFramePickerDialogFragment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;

public class ManualSlotCreationDialogFragment extends TimeFramePickerDialogFragment implements RangeSlider.OnSliderTouchListener, RangeSlider.OnChangeListener {
    public interface IListener {
        void onManualSlotCreated(FreeManualTimeWindow timeWindow);
    }

    public static class FreeManualTimeWindow implements ITimeFrame {

        public final ManualSlotBlueprint blueprint;
        public final LocalDate date;
        public final LocalTime fromTime;
        public final LocalTime toTime;

        public FreeManualTimeWindow(ManualSlotBlueprint blueprint, LocalDate date, LocalTime fromTime, LocalTime toTime) {
            this.blueprint = blueprint;
            this.date = date;
            this.fromTime = fromTime;
            this.toTime = toTime;
        }

        @Override
        public LocalTime getStart() {
            return fromTime;
        }

        @Override
        public LocalTime getEnd() {
            return toTime;
        }
    }

    private final FreeManualTimeWindow timeWindow;
    Float lastValidStart;
    Float lastValidEnd;

    public ManualSlotCreationDialogFragment(FreeManualTimeWindow timeWindow, IListener listener) {
        super(timeWindow, timeWindow.blueprint.maxDuration, timeFrame -> {
            listener.onManualSlotCreated(new FreeManualTimeWindow(timeWindow.blueprint, timeWindow.date, timeFrame.getStart(), timeFrame.getEnd()));
        });
        this.timeWindow = timeWindow;
    }

    @Override
    public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
        float start = slider.getValues().get(0);
        float end = slider.getValues().get(1);
        long maxDuration = timeWindow.blueprint.maxDuration.toMinutes();
        float exceededDist = maxDuration - (end - start);

        if(exceededDist < 0) {
            if(start < lastValidStart)
                slider.setValues(start, end + exceededDist);
            else
                slider.setValues(start - exceededDist, end);
        } else {
            lastValidStart = start;
            lastValidEnd = end;
        }
    }

    @Override
    public void onStartTrackingTouch(@NonNull RangeSlider slider) {
        float start = slider.getValues().get(0);
        float end = slider.getValues().get(1);
        lastValidStart = start;
        lastValidEnd = end;
    }

    @Override
    public void onStopTrackingTouch(@NonNull RangeSlider slider) {

    }

    protected RangeSlider initView(View view) {
        RangeSlider slider = super.initView(view);
        slider.addOnChangeListener(this);
        slider.addOnSliderTouchListener(this);
        return slider;
    }
}
