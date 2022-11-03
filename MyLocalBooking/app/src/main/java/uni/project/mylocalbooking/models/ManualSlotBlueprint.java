package uni.project.mylocalbooking.models;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.HashSet;

public class ManualSlotBlueprint extends SlotBlueprint {
    private final Long id;
    public final LocalTime openTime;
    public final LocalTime closeTime;
    public final Duration maxDuration;

    public ManualSlotBlueprint(Long id, LocalTime openTime, LocalTime closeTime, Duration maxDuration, Long slot_id, Establishment establishment, HashSet<DayOfWeek> weekdays, Integer reservationLimit, LocalDate fromDate, LocalDate toDate) {
        super(slot_id, establishment, weekdays, reservationLimit, fromDate, toDate);
        this.id = id;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.maxDuration = maxDuration;
    }

    public ManualSlotBlueprint(LocalTime openTime, LocalTime closeTime, Duration maxDuration, Establishment establishment, HashSet<DayOfWeek> weekdays, Integer reservationLimit, LocalDate fromDate, LocalDate toDate) {
        this(null, openTime, closeTime, maxDuration, null, establishment, weekdays, reservationLimit, fromDate, toDate);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
