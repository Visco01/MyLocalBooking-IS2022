package uni.project.mylocalbooking.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.HashSet;

public class PeriodicSlotBlueprint extends SlotBlueprint {
    private final Long id;
    public final LocalTime fromTime;
    public final LocalTime toTime;

    public PeriodicSlotBlueprint(Long id, LocalTime fromTime, LocalTime toTime, Long base_id, Establishment establishment, HashSet<DayOfWeek> weekdays, Integer reservationLimit, LocalDate fromDate, LocalDate toDate) {
        super(base_id, establishment, weekdays, reservationLimit, fromDate, toDate);
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public PeriodicSlotBlueprint(LocalTime fromTime, LocalTime toTime, Establishment establishment, HashSet<DayOfWeek> weekdays, Integer reservationLimit, LocalDate fromDate, LocalDate toDate) {
        this(null, fromTime, toTime, null, establishment, weekdays, reservationLimit, fromDate, toDate);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
