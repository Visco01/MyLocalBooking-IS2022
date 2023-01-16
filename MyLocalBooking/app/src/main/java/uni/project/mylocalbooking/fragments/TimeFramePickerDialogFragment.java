package uni.project.mylocalbooking.fragments;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.slider.RangeSlider;
import java.time.Duration;
import java.time.LocalTime;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.ITimeFrame;

public class TimeFramePickerDialogFragment extends DialogFragment {
    public interface IListener {
        void onTimeFrameCreated(ITimeFrame timeWindow);
    }

    private static final int TIME_GRANULARITY_MINUTES = 15;

    private final ITimeFrame timeFrame;
    private final IListener listener;
    private Duration maxDuration;

    public TimeFramePickerDialogFragment(ITimeFrame timeFrame, Duration maxDuration, IListener listener) {
        this.timeFrame = timeFrame;
        this.listener = listener;
        this.maxDuration = maxDuration;
    }

    public TimeFramePickerDialogFragment(ITimeFrame timeFrame, IListener listener) {
        this(timeFrame, null, listener);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.manual_slot_dialog, null);
        initView(view);
        builder.setView(view);
        builder.setTitle(R.string.create_manual_slot_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    listener.onTimeFrameCreated(parseView(view));
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    private ITimeFrame parseView(View view) {
        RangeSlider slider = view.findViewById(R.id.time_slider);
        LocalTime fromTime = timeFrame.getStart().plusMinutes(slider.getValues().get(0).intValue());
        LocalTime toTime = timeFrame.getEnd().plusMinutes(slider.getValues().get(1).intValue());

        return new ITimeFrame() {
            @Override
            public LocalTime getStart() {
                return fromTime;
            }

            @Override
            public LocalTime getEnd() {
                return toTime;
            }
        };
    }

    protected RangeSlider initView(View view) {
        RangeSlider slider = view.findViewById(R.id.time_slider);
        slider.setStepSize(TIME_GRANULARITY_MINUTES);
        slider.setMinSeparationValue(TIME_GRANULARITY_MINUTES);

        LocalTime start = timeFrame.getStart();
        LocalTime initialEnd = maxDuration == null ? timeFrame.getEnd() : start.plusMinutes(maxDuration.toMinutes());
        if(initialEnd.compareTo(timeFrame.getEnd()) > 0)
            initialEnd = timeFrame.getEnd();

        slider.setValueFrom(0);
        slider.setValueTo(Duration.between(start, timeFrame.getEnd()).getSeconds() / 60f);

        slider.setValues(0f, Duration.between(start, initialEnd).getSeconds() / 60f);
        slider.setLabelFormatter(value -> start.plusMinutes((int) value).toString());
        return slider;
    }
}
