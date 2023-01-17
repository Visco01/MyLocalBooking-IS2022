package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PeriodicSlotBlueprint extends SlotBlueprint implements ISelectableSlot {
    public static final Parcelable.Creator<PeriodicSlotBlueprint> CREATOR
            = new Parcelable.Creator<PeriodicSlotBlueprint>() {
        public PeriodicSlotBlueprint createFromParcel(Parcel in) {
            return new PeriodicSlotBlueprint(in);
        }

        public PeriodicSlotBlueprint[] newArray(int size) {
            return new PeriodicSlotBlueprint[size];
        }
    };

    public final LocalTime fromTime;
    public final LocalTime toTime;

    public final HashMap<LocalDate, PeriodicSlot> slots = new HashMap<>();

    public PeriodicSlotBlueprint(Long id, LocalTime fromTime, LocalTime toTime, Long base_id, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        super(id, base_id, establishment, reservationLimit, weekdays, fromDate, toDate);
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public PeriodicSlotBlueprint(LocalTime fromTime, LocalTime toTime, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        this(null, fromTime, toTime, null, establishment, reservationLimit, weekdays, fromDate, toDate);
    }

    public PeriodicSlotBlueprint(JSONObject object) throws JSONException {
        super(object);

        fromTime = LocalTime.parse(object.getString("from_time"));
        toTime = LocalTime.parse(object.getString("to_time"));
    }

    protected PeriodicSlotBlueprint(Parcel in) {
        super(in);
        fromTime = (LocalTime) in.readSerializable();
        toTime = (LocalTime) in.readSerializable();

        for(Parcelable s : in.readParcelableArray(Slot.class.getClassLoader())) {
            PeriodicSlot slot = (PeriodicSlot) s;
            addSlot(slot);
            slot.blueprint = this;
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeSerializable(fromTime);
        parcel.writeSerializable(toTime);

        PeriodicSlot[] slotsArr = new PeriodicSlot[slots.size()];
        slots.values().toArray(slotsArr);
        parcel.writeParcelableArray(slotsArr, i);
    }

    @Override
    public LocalTime getStart() {
        return fromTime;
    }

    @Override
    public LocalTime getEnd() {
        return toTime;
    }

    @Override
    public boolean isPasswordProtected() {
        return false;
    }

    @Override
    public Collection<Client> getAttending() {
        return null;
    }

    @Override
    public Integer getReservationLimit() {
        return super.reservationLimit;
    }

    @Override
    public void addSlot(Slot slot) {
        assert slot instanceof PeriodicSlot;

        slots.put(slot.date, (PeriodicSlot) slot);
    }

    @Override
    public void invalidateReservations(LocalDate date) {
        slots.remove(date);
    }

    public boolean overlapsWith(PeriodicSlotBlueprint other) {
        if(!super.overlapsWith(other))
            return false;

        return fromTime.isBefore(other.toTime) && other.fromTime.isBefore(toTime);
    }
}
