package uni.project.mylocalbooking.models;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public abstract class SlotBlueprint implements IDatabaseModel {
    private final Long id;
    public final Establishment establishment;
    public final HashSet<DayOfWeek> weekdays;
    public final Integer reservationLimit;
    public final Date fromDate;
    public final Date toDate;
    public final Collection<Slot> slots = new HashSet<>();

    public SlotBlueprint(Long id, Establishment establishment, HashSet<DayOfWeek> weekdays, Integer reservationLimit, Date fromDate, Date toDate) {
        this.id = id;
        this.establishment = establishment;
        this.weekdays = weekdays;
        this.reservationLimit = reservationLimit;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public SlotBlueprint(Establishment establishment, HashSet<DayOfWeek> weekdays, Integer reservationLimit, Date fromDate, Date toDate) {
        this(null, establishment, weekdays, reservationLimit, fromDate, toDate);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
