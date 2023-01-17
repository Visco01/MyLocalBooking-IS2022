package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ManualSlotBlueprint extends SlotBlueprint implements ITimeFrame {
    public static final Parcelable.Creator<ManualSlotBlueprint> CREATOR
            = new Parcelable.Creator<ManualSlotBlueprint>() {
        public ManualSlotBlueprint createFromParcel(Parcel in) {
            return new ManualSlotBlueprint(in);
        }

        public ManualSlotBlueprint[] newArray(int size) {
            return new ManualSlotBlueprint[size];
        }
    };

    public final LocalTime openTime;
    public final LocalTime closeTime;
    public final Duration maxDuration;

    public final HashMap<LocalDate, SortedSet<ManualSlot>> slots = new HashMap<>();

    public ManualSlotBlueprint(Long id, LocalTime openTime, LocalTime closeTime, Duration maxDuration, Long slot_id, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        super(id, slot_id, establishment, reservationLimit, weekdays, fromDate, toDate);
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.maxDuration = maxDuration;
    }

    public ManualSlotBlueprint(LocalTime openTime, LocalTime closeTime, Duration maxDuration, Establishment establishment,Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        this(null, openTime, closeTime, maxDuration, null, establishment, reservationLimit, weekdays, fromDate, toDate);
    }

    public ManualSlotBlueprint(JSONObject object, Establishment establishment) throws JSONException {
        super(object, establishment);

        openTime = LocalTime.parse(object.getString("open_time"));
        closeTime = LocalTime.parse(object.getString("close_time"));
        maxDuration = Duration.between(
                LocalTime.MIN,
                LocalTime.parse(object.getString("max_duration"))
        );
    }

    protected ManualSlotBlueprint(Parcel in) {
        super(in);
        openTime = (LocalTime) in.readSerializable();
        closeTime = (LocalTime) in.readSerializable();
        maxDuration = (Duration) in.readSerializable();

        for(Parcelable s : in.readParcelableArray(ManualSlot.class.getClassLoader())) {
            ManualSlot slot = (ManualSlot) s;
            slot.blueprint = this;
            addSlot(slot);
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeSerializable(openTime);
        parcel.writeSerializable(closeTime);
        parcel.writeSerializable(maxDuration);


        ManualSlot[] slotsArr = slots.values().stream().flatMap(SortedSet::stream).toArray(ManualSlot[]::new);
        parcel.writeParcelableArray(slotsArr, i);
    }

    @Override
    public LocalTime getStart() {
        return openTime;
    }

    @Override
    public LocalTime getEnd() {
        return closeTime;
    }

    @Override
    public void addSlot(Slot slot) {
        super.addSlot(slot);
        assert slot instanceof ManualSlot;

        if(!slots.containsKey(slot.date))
            slots.put(slot.date, new TreeSet<>());

        slots.get(slot.date).add((ManualSlot) slot);
    }

    @Override
    public void invalidateReservations(LocalDate date) {
        slots.remove(date);
    }

    public boolean overlapsWith(ManualSlotBlueprint other) {
        if(!super.overlapsWith(other))
            return false;

        return openTime.isBefore(other.closeTime) && other.openTime.isBefore(closeTime);
    }
}
