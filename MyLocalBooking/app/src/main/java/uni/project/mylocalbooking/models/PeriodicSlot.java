package uni.project.mylocalbooking.models;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.HashSet;

public class PeriodicSlot extends Slot {
    private final Long id;

    public PeriodicSlot(Long id, LocalTime fromTime, LocalTime toTime, Long slot_id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, SlotBlueprint blueprint) {
        super(slot_id, date, owner, passwordProtected, reservations, blueprint);
        this.id = id;
    }

    public PeriodicSlot(LocalTime fromTime, LocalTime toTime, LocalDate date, AppUser owner, SlotBlueprint blueprint) {
        this(null, fromTime, toTime, null, date, owner, false, new HashSet<>(), blueprint);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
