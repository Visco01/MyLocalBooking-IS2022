package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ManualSlot extends Slot implements IDatabaseSubclassModel, ISelectableSlot, Comparable<ManualSlot> {
    public static final Parcelable.Creator<ManualSlot> CREATOR
            = new Parcelable.Creator<ManualSlot>() {
        public ManualSlot createFromParcel(Parcel in) {
            return new ManualSlot(in);
        }

        public ManualSlot[] newArray(int size) {
            return new ManualSlot[size];
        }
    };

    private Long id;
    public final LocalTime fromTime;
    public final LocalTime toTime;

    public ManualSlot(Long id, LocalTime fromTime, LocalTime toTime, Long slot_id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, ManualSlotBlueprint blueprint) {
        super(slot_id, date, owner, passwordProtected, reservations, blueprint);
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        blueprint.addSlot(this);
    }

    public ManualSlot(LocalTime fromTime, LocalTime toTime, LocalDate date, AppUser owner, ManualSlotBlueprint blueprint) {
        this(null, fromTime, toTime, null, date, owner, false, new HashSet<>(), blueprint);
    }

    public ManualSlot(JSONObject object, HashMap<Long, SlotBlueprint> blueprints) throws JSONException {
        super(object, blueprints);

        id = object.getLong("subclass_id");
        fromTime = LocalTime.parse(object.getString("from_time"));
        toTime = LocalTime.parse(object.getString("to_time"));
    }

    protected ManualSlot(Parcel in) {
        super(in);
        id = in.readLong();
        fromTime = (LocalTime) in.readSerializable();
        toTime = (LocalTime) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(id);
        parcel.writeSerializable(fromTime);
        parcel.writeSerializable(toTime);
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
        return fromTime;
    }

    @Override
    public LocalTime getEnd() {
        return toTime;
    }

    @Override
    public boolean isPasswordProtected() {
        return super.passwordProtected;
    }

    @Override
    public Collection<Client> getAttending() {
        return super.reservations;
    }

    @Override
    public Integer getReservationLimit() {
        return super.blueprint.reservationLimit;
    }

    @Override
    public int compareTo(ManualSlot other) {
        int dateDiff = date.compareTo(other.date);
        if(dateDiff != 0)
            return dateDiff;

        return fromTime.compareTo(other.fromTime);
    }
}
