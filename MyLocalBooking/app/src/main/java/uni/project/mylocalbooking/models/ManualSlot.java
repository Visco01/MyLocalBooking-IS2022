package uni.project.mylocalbooking.models;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ManualSlot extends Slot implements ISelectableSlot, Comparable<ManualSlot> {
    private final Long id;
    public final LocalTime fromTime;
    public final LocalTime toTime;
    public final ManualSlotBlueprint blueprint;

    public ManualSlot(Long id, LocalTime fromTime, LocalTime toTime, Long slot_id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, ManualSlotBlueprint blueprint) {
        super(slot_id, date, owner, passwordProtected, reservations, blueprint);
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.blueprint = blueprint;

        SortedSet<ManualSlot> slots = blueprint.slots.get(super.date);
        boolean firstInsert = slots == null;
        if(firstInsert)
            slots = new TreeSet<>();

        slots.add(this);

        if(firstInsert)
            blueprint.slots.put(super.date, slots);
    }

    public ManualSlot(LocalTime fromTime, LocalTime toTime, LocalDate date, AppUser owner, ManualSlotBlueprint blueprint) {
        this(null, fromTime, toTime, null, date, owner, false, new HashSet<>(), blueprint);
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public LocalTime getStart() {
        return fromTime;
    }

    @Override
    public LocalTime getEnd() {
        return toTime;
    }

    @Override
    public boolean isPasswordProtected() {
        return super.passwordProtected;
    }

    @Override
    public Collection<Client> getAttending() {
        return super.reservations;
    }

    @Override
    public Integer getReservationLimit() {
        return super.blueprint.reservationLimit;
    }

    @Override
    public int compareTo(ManualSlot other) {
        int dateDiff = date.compareTo(other.date);
        if(dateDiff != 0)
            return dateDiff;

        return fromTime.compareTo(other.fromTime);
    }
}
