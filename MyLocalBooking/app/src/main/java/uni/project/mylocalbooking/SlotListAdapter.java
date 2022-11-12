package uni.project.mylocalbooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.models.ISlotListElement;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListAdapter extends BaseAdapter {
    private final SlotListViewModel viewModel;
    private List<ISlotListElement> filteredSlots = new ArrayList<>();

    public SlotListAdapter(SlotListViewModel viewModel) {
        super();
        this.viewModel = viewModel;
    }
    @Override
    public int getCount() {
        return filteredSlots.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredSlots.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View slotRoot = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.slot, viewGroup, false);

        ISlotListElement slot = filteredSlots.get(i);
        ((TextView) slotRoot.findViewById(R.id.from_time)).setText(slot.getFromTime().toString());
        ((TextView) slotRoot.findViewById(R.id.to_time)).setText(slot.getToTime().toString());

        Integer reservationLimit = slot.getReservationLimit() != null ? slot.getReservationLimit() : 0;
        int attending = slot.getAttending() != null ? slot.getAttending().size() : 0;
        int available = reservationLimit - attending;

        if(reservationLimit != 0) {
            ((TextView) slotRoot.findViewById(R.id.available_reservations)).setText(Integer.toString(available));
            ((TextView) slotRoot.findViewById(R.id.places_text)).setText(R.string.places_available_of);
            ((TextView) slotRoot.findViewById(R.id.max_reservations)).setText(reservationLimit.toString());
        }
        else {
            ((TextView) slotRoot.findViewById(R.id.available_reservations)).setText(Integer.toString(attending));
            ((TextView) slotRoot.findViewById(R.id.places_text)).setText(R.string.people_attending);
            ((TextView) slotRoot.findViewById(R.id.available_reservations)).setText(Integer.toString(attending));
        }

        slotRoot.findViewById(R.id.side_line).setBackgroundResource(!slot.isInstance() || available > 0 ?  R.color.slot_line_available : R.color.slot_line_unavailable);

        return slotRoot;
    }

    public void refresh(DayOfWeek dow) {
        filteredSlots.clear();

        LocalDate weekStart = viewModel.getStartOfWeek().getValue();
        LocalDate selected = weekStart.plusDays(dow.getValue() - 1);
        this.filteredSlots = viewModel.getBlueprints(selected).stream().filter(b -> b instanceof ISlotListElement)
                .map(b -> (ISlotListElement) b).collect(Collectors.toList());

        for(final SlotBlueprint bp : viewModel.getBlueprints(selected)) {
            if(bp instanceof ManualSlotBlueprint) {
                List<ManualSlot> scheduledForSelected = ((ManualSlotBlueprint) bp).slots.get(selected);
                if(scheduledForSelected == null)
                    continue;

                filteredSlots.addAll(scheduledForSelected);

            } else {
                PeriodicSlot scheduledForSelected = ((PeriodicSlotBlueprint) bp).slots.get(selected);
                if(scheduledForSelected != null)
                    filteredSlots.add(scheduledForSelected);
                else
                    filteredSlots.add((PeriodicSlotBlueprint) bp);
            }
        }

        notifyDataSetChanged();
    }
}
