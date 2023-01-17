package uni.project.mylocalbooking.activities.client;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.ITimeFrame;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListAdapter extends BaseAdapter {
    public interface IListener {
        void onSlotReservationToggled(ISelectableSlot slot);
        void onManualSlotCreate(ManualSlotCreationDialogFragment.FreeManualTimeWindow timeWindow);
    }

    private static final int TYPE_SLOT = 0;
    private static final int TYPE_TIMEFRAME = 1;

    private final IListener listener;
    public final List<ITimeFrame> filteredSlots = new ArrayList<>();
    private LocalDate currentDate;

    public SlotListAdapter(IListener listener) {
        super();
        this.listener = listener;
    }

    public void onRefresh(LocalDate currentDate, Collection<SlotBlueprint> blueprints) {
        this.currentDate = currentDate;

        filteredSlots.clear();

        for(final SlotBlueprint item : blueprints) {
            if(item instanceof ManualSlotBlueprint) {
                ManualSlotBlueprint blueprint = (ManualSlotBlueprint) item;
                filteredSlots.addAll(extractTimeframes(blueprint));
            } else {
                PeriodicSlot scheduledForSelected = ((PeriodicSlotBlueprint) item).slots.get(currentDate);
                if(scheduledForSelected != null)
                    filteredSlots.add(scheduledForSelected);
                else
                    filteredSlots.add((PeriodicSlotBlueprint) item);
            }
        }

        filteredSlots.sort(Comparator.comparing(ITimeFrame::getStart));

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredSlots.size();
    }

    @Override
    public int getItemViewType(int i) {
        return filteredSlots.get(i) instanceof ISelectableSlot ? TYPE_SLOT : TYPE_TIMEFRAME;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
        int viewType = getItemViewType(i);
        View viewRoot = LayoutInflater.from(viewGroup.getContext())
                .inflate(viewType == TYPE_SLOT ? R.layout.slot_list_item : R.layout.manual_blueprint_list_item, viewGroup, false);

        if(viewType == TYPE_SLOT)
            createSlotElement(i, viewRoot);
        else
            createAvailableTimeWindow(i, viewRoot);

        return viewRoot;
    }

    private List<ITimeFrame> extractTimeframes(ManualSlotBlueprint blueprint) {
        SortedSet<ManualSlot> slots = blueprint.slots.get(currentDate);
        List<ITimeFrame> results = new ArrayList<>();

        if(slots == null) {
            results.add(blueprint);
            return results;
        }

        LocalTime previous = blueprint.openTime;
        for(ManualSlot slot : slots) {
            if(previous.compareTo(slot.fromTime) < 0) {
                long nano = previous.toNanoOfDay();
                results.add(new ManualSlotCreationDialogFragment.FreeManualTimeWindow(
                        blueprint,
                        currentDate,
                        LocalTime.ofNanoOfDay(nano),
                        slot.fromTime
                ));
            }
            results.add(slot);
            previous = slot.toTime;
        }

        long nano = previous.toNanoOfDay();
        if(previous.compareTo(blueprint.closeTime) < 0)
            results.add(new ManualSlotCreationDialogFragment.FreeManualTimeWindow(
                    blueprint,
                    currentDate,
                    LocalTime.ofNanoOfDay(nano),
                    blueprint.closeTime
            ));

        return results;
    }

    private void createSlotElement(int i, View slotRoot) {
        ISelectableSlot slot = (ISelectableSlot) filteredSlots.get(i);
        ((TextView) slotRoot.findViewById(R.id.from_time)).setText(slot.getStart().toString());
        ((TextView) slotRoot.findViewById(R.id.to_time)).setText(slot.getEnd().toString());

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

        LocalDateTime slotStart = LocalDateTime.of(currentDate, slot.getStart());
        LocalDateTime now = LocalDateTime.now();
        boolean future = now.compareTo(slotStart) < 0;
        boolean userHasReservation = slot instanceof Slot && ((Slot) slot).reservations.contains(MyLocalBooking.getCurrentUser());
        boolean bookable = future && (userHasReservation || reservationLimit == null || attending < reservationLimit);

        if(!future)
            slotRoot.findViewById(R.id.side_line).setBackgroundResource(R.color.slot_line_expired);
        else {
            int colorId = bookable ? R.color.slot_line_available : R.color.slot_line_unavailable;
            if(bookable && attending == 0)
                colorId = R.color.slot_line_free;
            slotRoot.findViewById(R.id.side_line).setBackgroundResource(colorId);
        }

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
        if(userHasReservation) {
            reservationButton.setText(R.string.dash);
            reservationButton.setBackgroundTintList(ColorStateList.valueOf(0xFF8BC34A));
        }

        reservationButton.setOnClickListener(v -> {
            listener.onSlotReservationToggled(slot);
        });
    }

    private void createAvailableTimeWindow(int i, View viewRoot) {
        ITimeFrame timeFrame = filteredSlots.get(i);

        if(timeFrame instanceof ManualSlotBlueprint) {
            ManualSlotBlueprint blueprint = (ManualSlotBlueprint) timeFrame;
            timeFrame = new ManualSlotCreationDialogFragment.FreeManualTimeWindow(blueprint, currentDate, blueprint.openTime, blueprint.closeTime);
        }

        ((TextView) viewRoot.findViewById(R.id.open_time)).setText(timeFrame.getStart().toString());
        ((TextView) viewRoot.findViewById(R.id.close_time)).setText(timeFrame.getEnd().toString());

        final ManualSlotCreationDialogFragment.FreeManualTimeWindow window = (ManualSlotCreationDialogFragment.FreeManualTimeWindow) timeFrame;

        AppCompatButton button = viewRoot.findViewById(R.id.create_slot_button);
        LocalDateTime slotStart = LocalDateTime.of(currentDate, timeFrame.getStart());
        LocalDateTime now = LocalDateTime.now();
        button.setOnClickListener(view -> listener.onManualSlotCreate(window));

        boolean isOld = now.compareTo(slotStart) >= 0;
        button.setEnabled(!isOld);
        button.setBackgroundColor(isOld ? 0x68CCB5B5 : 0xFF8BC34A);
        ((TextView) viewRoot.findViewById(R.id.timeframe_title)).setText(isOld ? R.string.no_longer_available : R.string.available_for_booking);
    }
}
