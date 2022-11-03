package uni.project.mylocalbooking.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public abstract class Slot implements IDatabaseModel  {
    private final Long id;
    public final Date date;
    public boolean passwordProtected;
    public final AppUser owner;
    public final HashSet<Client> reservations;
    public final SlotBlueprint blueprint;

    public Slot(Long id, Date date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, SlotBlueprint blueprint) {
        this.id = id;
        this.date = date;
        this.owner = owner;
        this.passwordProtected = passwordProtected;
        this.reservations = reservations;
        this.blueprint = blueprint;

        if (blueprint.slots == null)
            blueprint.slots = new ArrayList<>();
        blueprint.slots.add(this);
    }

    public Slot(Date date, AppUser owner, SlotBlueprint blueprint) {
        this(null, date, owner, false, new HashSet<>(), blueprint);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
