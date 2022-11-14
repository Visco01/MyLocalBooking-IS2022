package uni.project.mylocalbooking.activites.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListAdapter extends BaseAdapter {
    public interface ISlotListListener {
        void onReservationToggled(ISelectableSlot slot);
    }

    private final ISlotListListener listener;
    private final List<ISelectableSlot> filteredSlots = new ArrayList<>();

    public SlotListAdapter(ISlotListListener listener) {
        super();
        this.listener = listener;
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

        ISelectableSlot slot = filteredSlots.get(i);
        ((TextView) slotRoot.findViewById(R.id.from_time)).setText(slot.getFromTime().toString());
        ((TextView) slotRoot.findViewById(R.id.to_time)).setText(slot.getToTime().toString());

        Integer reservationLimit = slot.getReservationLimit();
        int attending = slot.getAttending() != null ? slot.getAttending().size() : 0;

        if(reservationLimit != null) {
            ((TextView) slotRoot.findViewById(R.id.available_reservations)).setText(Integer.toString(reservationLimit - attending));
            ((TextView) slotRoot.findViewById(R.id.places_text)).setText(R.string.places_available_of);
            ((TextView) slotRoot.findViewById(R.id.max_reservations)).setText(reservationLimit.toString());
        }
        else {
            ((TextView) slotRoot.findViewById(R.id.available_reservations)).setText(Integer.toString(attending));
            ((TextView) slotRoot.findViewById(R.id.places_text)).setText(R.string.people_attending);
            ((TextView) slotRoot.findViewById(R.id.available_reservations)).setText(Integer.toString(attending));
        }

        boolean bookable = reservationLimit == null || attending < reservationLimit;
        slotRoot.findViewById(R.id.side_line).setBackgroundResource(bookable ?  R.color.slot_line_available : R.color.slot_line_unavailable);

        if(slot.isPasswordProtected()) {
            if(bookable)
                slotRoot.findViewById(R.id.side_line).setBackgroundResource(R.color.slot_line_locked);
        }
        else
            slotRoot.findViewById(R.id.slot_padlock).setVisibility(View.GONE);

        if(!bookable) {
            slotRoot.findViewById(R.id.reservation_button).setVisibility(View.GONE);
            int color = 0x1F000000;
            ((TextView) slotRoot.findViewById(R.id.from_time)).setTextColor(color);
            ((TextView) slotRoot.findViewById(R.id.to_time)).setTextColor(color);
            ((TextView) slotRoot.findViewById(R.id.time_text)).setTextColor(color);
            ((TextView) slotRoot.findViewById(R.id.available_reservations)).setTextColor(color);
            ((TextView) slotRoot.findViewById(R.id.places_text)).setTextColor(color);
            ((TextView) slotRoot.findViewById(R.id.max_reservations)).setTextColor(color);
        }

        Button reservationButton = ((Button) slotRoot.findViewById(R.id.reservation_button));
        // TODO: style button based on if the user is booked or not

        reservationButton.setOnClickListener(v -> {
            // TODO: toggle style
            listener.onReservationToggled(slot);
        });

        return slotRoot;
    }

    public void onRefresh(LocalDate selected, List<SlotBlueprint> blueprints) {
        filteredSlots.clear();

        for(final SlotBlueprint item : blueprints) {
            if(item instanceof ManualSlotBlueprint) {
                List<ManualSlot> scheduledForSelected = ((ManualSlotBlueprint) item).slots.get(selected);
                if(scheduledForSelected == null)
                    continue;

                for(ManualSlot slot : scheduledForSelected)
                    filteredSlots.add(slot);

            } else {
                PeriodicSlot scheduledForSelected = ((PeriodicSlotBlueprint) item).slots.get(selected);
                if(scheduledForSelected != null)
                    filteredSlots.add(scheduledForSelected);
                else
                    filteredSlots.add((PeriodicSlotBlueprint) item);
            }
        }

        filteredSlots.sort(Comparator.comparing(ISelectableSlot::getFromTime));

        notifyDataSetChanged();
    }
}
