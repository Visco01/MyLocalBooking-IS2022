package uni.project.mylocalbooking.models;

import java.time.LocalTime;
import java.util.Date;
import java.util.HashSet;

public class ManualSlot extends Slot {
    private final Long id;
    public final LocalTime fromTime;
    public final LocalTime toTime;

    public ManualSlot(Long id, LocalTime fromTime, LocalTime toTime, Long slot_id, Date date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, SlotBlueprint blueprint) {
        super(slot_id, date, owner, passwordProtected, reservations, blueprint);
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public ManualSlot(LocalTime fromTime, LocalTime toTime, Date date, AppUser owner, SlotBlueprint blueprint) {
        this(null, fromTime, toTime, null, date, owner, false, new HashSet<>(), blueprint);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}