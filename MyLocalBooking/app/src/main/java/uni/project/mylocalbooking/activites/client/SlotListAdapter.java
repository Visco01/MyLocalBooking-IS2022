package uni.project.mylocalbooking.activites.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import androidx.fragment.app.FragmentActivity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.SlotPasswordDialogFragment;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;

public class SlotListAdapter extends BaseAdapter {
    public interface ISlotListElement {
        LocalTime getFromTime();
        LocalTime getToTime();
        boolean isPasswordProtected();
        Collection<Client> getAttending();
        Integer getReservationLimit();
    }


    private final SlotListViewModel viewModel;
    private final FragmentActivity activity;
    private final List<ISlotListElement> filteredSlots = new ArrayList<>();

    public SlotListAdapter(FragmentActivity activity, SlotListViewModel viewModel) {
        super();
        this.activity = activity;
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
            //slot.toggleReservation();

            if(slot.isPasswordProtected()) {
                SlotPasswordDialogFragment dialog = new SlotPasswordDialogFragment();
                dialog.show(activity.getSupportFragmentManager(), "NoticeDialogFragment");
            }

            // TODO: toggle style
        });

        return slotRoot;
    }

    public void refresh(DayOfWeek dow) {
        filteredSlots.clear();

        LocalDate weekStart = viewModel.getStartOfWeek().getValue();
        LocalDate selected = weekStart.plusDays(dow.getValue() - 1);
        filteredSlots.clear();
        List<ISlotListElement> slotItems = viewModel.getBlueprints(selected).stream().filter(b -> b instanceof ISlotListElement)
                .map(b -> (ISlotListElement) b).collect(Collectors.toList());

        for(final ISlotListElement item : slotItems) {
            if(item instanceof ManualSlotBlueprint) {
                List<ManualSlot> scheduledForSelected = ((ManualSlotBlueprint) item).slots.get(selected);
                if(scheduledForSelected == null)
                    continue;

                filteredSlots.addAll(scheduledForSelected);

            } else {
                PeriodicSlot scheduledForSelected = ((PeriodicSlotBlueprint) item).slots.get(selected);
                if(scheduledForSelected != null)
                    filteredSlots.add(scheduledForSelected);
                else
                    filteredSlots.add((PeriodicSlotBlueprint) item);
            }
        }

        filteredSlots.sort(Comparator.comparing(ISlotListElement::getFromTime));

        notifyDataSetChanged();
    }
}
