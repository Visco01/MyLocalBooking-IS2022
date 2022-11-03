package uni.project.mylocalbooking.models;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.time.LocalDate;
import java.util.HashSet;

public abstract class SlotBlueprint implements IDatabaseModel {
    private final Long id;
    public final Establishment establishment;
    public final HashSet<DayOfWeek> weekdays;
    public final Integer reservationLimit;
    public final LocalDate fromDate;
    public final LocalDate toDate;

    public Collection<Slot> slots;

    public SlotBlueprint(Long id, Establishment establishment, HashSet<DayOfWeek> weekdays, Integer reservationLimit, LocalDate fromDate, LocalDate toDate) {
        this.id = id;
        this.weekdays = weekdays;
        this.reservationLimit = reservationLimit;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.establishment = establishment;

        if(establishment.blueprints == null)
            establishment.blueprints = new ArrayList<>();
        establishment.blueprints.add(this);
    }

    public SlotBlueprint(Establishment establishment, HashSet<DayOfWeek> weekdays, Integer reservationLimit, LocalDate fromDate, LocalDate toDate) {
        this(null, establishment, weekdays, reservationLimit, fromDate, toDate);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
