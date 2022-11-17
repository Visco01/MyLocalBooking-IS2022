package uni.project.mylocalbooking.activities.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.slider.RangeSlider;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.ITimeFrame;

public class CreateManualSlotDialog extends DialogFragment {
    public interface IListener {
        void onManualSlotCreated(ITimeFrame timeFrame);
    }

    private static final int TIME_GRANULARITY_MINUTES = 15;

    private final ITimeFrame timeFrame;
    private final IListener listener;

    public CreateManualSlotDialog(ITimeFrame timeFrame, IListener listener) {
        this.timeFrame = timeFrame;
        this.listener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
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

    private ITimeFrame parseView(View view) {
        RangeSlider slider = (RangeSlider) view.findViewById(R.id.time_slider);
        return new ITimeFrame() {
            @Override
            public LocalTime getStart() {
                return timeFrame.getStart().plusMinutes((int) slider.getValueFrom());
            }

            @Override
            public LocalTime getEnd() {
                return timeFrame.getStart().plusMinutes((int) slider.getValueTo());
            }
        };
    }

    private void initView(View view) {
        RangeSlider slider = (RangeSlider) view.findViewById(R.id.time_slider);
        slider.setStepSize(TIME_GRANULARITY_MINUTES);
        slider.setMinSeparationValue(TIME_GRANULARITY_MINUTES);

        int deltaSeconds = (int) Duration.between(timeFrame.getStart(), timeFrame.getEnd()).getSeconds();
        int maxVal = deltaSeconds / 60;
        slider.setValueFrom(0);
        slider.setValueTo(maxVal);

        slider.setValues((float) 0, (float) maxVal);

    }
}
