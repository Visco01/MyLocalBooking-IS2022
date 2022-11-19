package uni.project.mylocalbooking.models;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.HashSet;

public class PeriodicSlot extends Slot implements IDatabaseSubclassModel {
    private Long id;

    public PeriodicSlot(Long id, Long slot_id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, SlotBlueprint blueprint) {
        super(slot_id, date, owner, passwordProtected, reservations, blueprint);
        this.id = id;
    }

    public PeriodicSlot(LocalDate date, AppUser owner, SlotBlueprint blueprint) {
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
}
