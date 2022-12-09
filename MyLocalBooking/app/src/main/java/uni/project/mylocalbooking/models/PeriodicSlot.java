package uni.project.mylocalbooking.models;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

public class PeriodicSlot extends Slot implements IDatabaseSubclassModel, ISelectableSlot {
    private Long id;

    public PeriodicSlot(Long id, Long slot_id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, PeriodicSlotBlueprint blueprint) {
        super(slot_id, date, owner, passwordProtected, reservations, blueprint);
        this.id = id;
        blueprint.slots.put(super.date, this);
    }

    public PeriodicSlot(LocalDate date, AppUser owner, PeriodicSlotBlueprint blueprint) {
        this(null, null, date, owner, false, new HashSet<>(), blueprint);
    }

    @Override
    public Long getSubclassId() {
        return id;
    }

    @Override
    public void setSubclassId(Long id) {
        this.id = id;
    }

    @Override
    public LocalTime getStart() {
        return ((PeriodicSlotBlueprint) super.blueprint).fromTime;
    }

    @Override
    public LocalTime getEnd() {
        return ((PeriodicSlotBlueprint) super.blueprint).toTime;
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
}
