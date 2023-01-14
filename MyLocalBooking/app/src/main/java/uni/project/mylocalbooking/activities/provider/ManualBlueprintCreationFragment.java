package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.TimeFramePickerDialogFragment;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;


public class ManualBlueprintCreationFragment extends BlueprintCreationFragment {
    private class AvailableBlueprintsAdapter extends BaseAdapter {
        private final List<ITimeFrame> availableTimeframes;
        private AvailableBlueprintsAdapter(Collection<ManualSlotBlueprint> blueprints) {
            this.availableTimeframes = extractTimeFrames(blueprints);
        }

        private List<ITimeFrame> extractTimeFrames(Collection<ManualSlotBlueprint> blueprints) {
            List<ITimeFrame> results = new ArrayList<>();

            LocalTime previous = LocalTime.MIN;
            for(ManualSlotBlueprint blueprint : blueprints) {
                if(previous.compareTo(blueprint.openTime) < 0) {
                    final LocalTime start = previous;
                    final LocalTime end = blueprint.openTime;
                    results.add(new ITimeFrame() {
                        @Override
                        public LocalTime getStart() {
                            return start;
                        }

                        @Override
                        public LocalTime getEnd() {
                            return end;
                        }
                    });
                }
                previous = blueprint.closeTime;
            }

            // TODO: right bound should allow 00:00
            LocalTime maxTime = LocalTime.of(23, 59);
            if(previous.compareTo(maxTime) < 0) {
                final LocalTime start = previous;
                results.add(new ITimeFrame() {

                    @Override
                    public LocalTime getStart() {
                        return start;
                    }

                    @Override
                    public LocalTime getEnd() {
                        return maxTime;
                    }
                });
            }
            return results;
        }

        @Override
        public int getCount() {
            return availableTimeframes.size();
        }

        @Override
        public Object getItem(int i) {
            return availableTimeframes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.manual_blueprint_list_item, viewGroup, false);

            ITimeFrame timeFrame = availableTimeframes.get(i);
            ((TextView) view.findViewById(R.id.open_time)).setText(timeFrame.getStart().toString());
            ((TextView) view.findViewById(R.id.close_time)).setText(timeFrame.getEnd().toString());

            Button button = view.findViewById(R.id.create_slot_button);
            button.setText(R.string.create_blueprint);
            button.setOnClickListener(v -> onAddBlueprint(timeFrame));

            return view;
        }


    }
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
            System.out.println(); //TODO: REMOVE
        });

        list.setAdapter(adapter);
    }

    private void onAddBlueprint(ITimeFrame timeFrame) {
        TimeFramePickerDialogFragment slotCreationDialog = new TimeFramePickerDialogFragment(timeFrame, newTimeFrame -> {
            System.out.println(); //TODO: REMOVE
        });
        slotCreationDialog.show(getChildFragmentManager(), "AddBlueprintDialog");
    }
}