package uni.project.mylocalbooking.activities.provider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.util.stream.Collectors;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.TimeFramePickerDialogFragment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;


public class ManualBlueprintCreationFragment extends BlueprintCreationFragment {
    private static final String TITLE_TIME = "time";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout list = view.findViewById(R.id.blueprint_creation_steps_layout);

        addTimeFramePicker(list);

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

    protected void onAddBlueprint(ITimeFrame timeFrame) {
        TimeFramePickerDialogFragment slotCreationDialog = new TimeFramePickerDialogFragment(timeFrame, newTimeFrame -> {
            System.out.println(); //TODO: save result somewhere and wait for the next button to be pressed
        });
        slotCreationDialog.show(getChildFragmentManager(), "AddBlueprintDialog");
    }
}