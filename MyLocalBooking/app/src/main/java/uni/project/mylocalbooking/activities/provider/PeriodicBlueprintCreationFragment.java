package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;
import uni.project.mylocalbooking.fragments.TimeFramePickerDialogFragment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;

public class PeriodicBlueprintCreationFragment extends BlueprintCreationFragment {
    private PeriodicSlotBlueprint blueprint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addTimeFramePicker(cardView -> {
            super.end(blueprint);
        });
    }

    protected void onAddBlueprint(ITimeFrame timeFrame) {
        TimeFramePickerDialogFragment slotCreationDialog = new TimeFramePickerDialogFragment(timeFrame, newTimeFrame -> {
            blueprint = new PeriodicSlotBlueprint(newTimeFrame.getStart(), newTimeFrame.getEnd(), establishment, reservationLimit, weekDays, fromDate, toDate);
        });
        slotCreationDialog.show(getChildFragmentManager(), "AddBlueprintDialog");
    }
}
