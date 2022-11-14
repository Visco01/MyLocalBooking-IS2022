package uni.project.mylocalbooking.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class PeriodicSlotBlueprint extends SlotBlueprint implements ISelectableSlot {
    private final Long id;
    public final LocalTime fromTime;
    public final LocalTime toTime;

    public final HashMap<LocalDate, PeriodicSlot> slots = new HashMap<>();

    public PeriodicSlotBlueprint(Long id, LocalTime fromTime, LocalTime toTime, Long base_id, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        super(base_id, establishment, reservationLimit, weekdays, fromDate, toDate);
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public PeriodicSlotBlueprint(LocalTime fromTime, LocalTime toTime, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        this(null, fromTime, toTime, null, establishment, reservationLimit, weekdays, fromDate, toDate);
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public LocalTime getFromTime() {
        return fromTime;
    }

    @Override
    public LocalTime getToTime() {
        return toTime;
    }

    @Override
    public boolean isPasswordProtected() {
        return false;
    }

    @Override
    public Collection<Client> getAttending() {
        return null;
    }

    @Override
    public Integer getReservationLimit() {
        return super.reservationLimit;
    }
}
