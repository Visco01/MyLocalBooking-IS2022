package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uni.project.mylocalbooking.fragments.TimeFramePickerDialogFragment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;

public class PeriodicBlueprintCreationFragment extends BlueprintCreationFragment {
    private static final String TITLE_TIME = "Time window";
    private PeriodicSlotBlueprint blueprint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addTimeFramePicker(TITLE_TIME, view -> {
            super.end(blueprint);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void onAddBlueprint(ITimeFrame timeFrame) {
        TimeFramePickerDialogFragment slotCreationDialog = new TimeFramePickerDialogFragment(timeFrame, newTimeFrame -> {
            blueprint = new PeriodicSlotBlueprint(newTimeFrame.getStart(), newTimeFrame.getEnd(), establishment, reservationLimit, weekDays, fromDate, toDate);
        });
        slotCreationDialog.show(getChildFragmentManager(), "AddBlueprintDialog");
    }
}
