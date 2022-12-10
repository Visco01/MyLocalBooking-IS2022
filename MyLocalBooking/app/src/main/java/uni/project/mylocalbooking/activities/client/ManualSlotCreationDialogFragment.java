package uni.project.mylocalbooking.activities.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;

public class ManualSlotCreationDialogFragment extends DialogFragment {
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

    private static final int TIME_GRANULARITY_MINUTES = 15;

    private final FreeManualTimeWindow timeWindow;
    private final IListener listener;

    public ManualSlotCreationDialogFragment(FreeManualTimeWindow timeWindow, IListener listener) {
        this.timeWindow = timeWindow;
        this.listener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.manual_slot_dialog, null);
        initView(view);
        builder.setView(view);
        builder.setTitle(R.string.create_manual_slot_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    listener.onManualSlotCreated(parseView(view));
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    private FreeManualTimeWindow parseView(View view) {
        RangeSlider slider = view.findViewById(R.id.time_slider);
        LocalTime fromTime = timeWindow.fromTime.plusMinutes(slider.getValues().get(0).intValue());
        LocalTime toTime = timeWindow.fromTime.plusMinutes(slider.getValues().get(1).intValue());

        return new FreeManualTimeWindow(timeWindow.blueprint, timeWindow.date, fromTime, toTime);
    }

    private void initView(View view) {
        RangeSlider slider = view.findViewById(R.id.time_slider);
        slider.setStepSize(TIME_GRANULARITY_MINUTES);
        slider.setMinSeparationValue(TIME_GRANULARITY_MINUTES);

        LocalTime start = timeWindow.getStart();
        int deltaSeconds = (int) Duration.between(start, timeWindow.getEnd()).getSeconds();
        int maxVal = deltaSeconds / 60;
        slider.setValueFrom(0);
        slider.setValueTo(maxVal);

        slider.setValues((float) 0, (float) maxVal);
        slider.setLabelFormatter(value -> start.plusMinutes((int) value).toString());
    }
}
