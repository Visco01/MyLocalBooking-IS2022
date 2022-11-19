package uni.project.mylocalbooking.models;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.time.LocalDate;
import java.util.HashSet;

public abstract class SlotBlueprint extends DatabaseModel {
    public final Establishment establishment;
    public final HashSet<DayOfWeek> weekdays;
    public final Integer reservationLimit;
    public final LocalDate fromDate;
    public final LocalDate toDate;

    public Collection<Slot> slots;

    public SlotBlueprint(Long id, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        super(id);
        this.weekdays = weekdays;
        this.reservationLimit = reservationLimit;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.establishment = establishment;

        if(establishment.blueprints == null)
            establishment.blueprints = new ArrayList<>();
        establishment.blueprints.add(this);
    }

    public SlotBlueprint(Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        this(null, establishment, reservationLimit, weekdays, fromDate, toDate);
    }
}
