package uni.project.mylocalbooking.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.HashSet;

public class PeriodicSlotBlueprint extends SlotBlueprint implements IDatabaseSubclassModel {
    private Long id;
    public final LocalTime fromTime;
    public final LocalTime toTime;

    public PeriodicSlotBlueprint(Long id, LocalTime fromTime, LocalTime toTime, Long base_id, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        super(base_id, establishment, reservationLimit, weekdays, fromDate, toDate);
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public PeriodicSlotBlueprint(LocalTime fromTime, LocalTime toTime, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        this(null, fromTime, toTime, null, establishment, reservationLimit, weekdays, fromDate, toDate);
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
