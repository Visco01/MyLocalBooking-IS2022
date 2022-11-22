package uni.project.mylocalbooking.models;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

public class ManualSlotBlueprint extends SlotBlueprint implements IDatabaseSubclassModel, ITimeFrame {
    private Long id;
    public final LocalTime openTime;
    public final LocalTime closeTime;
    public final Duration maxDuration;

    public final HashMap<LocalDate, SortedSet<ManualSlot>> slots = new HashMap<>();

    public ManualSlotBlueprint(Long id, LocalTime openTime, LocalTime closeTime, Duration maxDuration, Long slot_id, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        super(slot_id, establishment, reservationLimit, weekdays, fromDate, toDate);
        this.id = id;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.maxDuration = maxDuration;
    }

    public ManualSlotBlueprint(LocalTime openTime, LocalTime closeTime, Duration maxDuration, Establishment establishment,Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        this(null, openTime, closeTime, maxDuration, null, establishment, reservationLimit, weekdays, fromDate, toDate);
    }

    @Override
    public Long getSubclassId() {
        return id;
    }

    @Override
    public void setSubclassId(Long id) {
        this.id = id;
    }

    @Override
    public LocalTime getStart() {
        return openTime;
    }

    @Override
    public LocalTime getEnd() {
        return closeTime;
    }
}
