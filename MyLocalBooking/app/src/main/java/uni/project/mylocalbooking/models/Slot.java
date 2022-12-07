package uni.project.mylocalbooking.models;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.HashSet;

public abstract class Slot extends DatabaseModel {
    public final LocalDate date;
    public boolean passwordProtected;
    public final AppUser owner;
    public HashSet<Client> reservations;
    public final SlotBlueprint blueprint;

    public Slot(Long id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, SlotBlueprint blueprint) {
        super(id);
        this.date = date;
        this.owner = owner;
        this.passwordProtected = passwordProtected;
        this.reservations = reservations;
        this.blueprint = blueprint;

        if (blueprint.slots == null)
            blueprint.slots = new ArrayList<>();
        blueprint.slots.add(this);
    }

    public Slot(LocalDate date, AppUser owner, SlotBlueprint blueprint) {
        this(null, date, owner, false, new HashSet<>(), blueprint);
    }
}
