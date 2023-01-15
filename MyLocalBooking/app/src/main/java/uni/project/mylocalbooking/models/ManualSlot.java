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

public class ManualSlot extends Slot implements ISelectableSlot, Comparable<ManualSlot> {
    public static final Parcelable.Creator<ManualSlot> CREATOR
            = new Parcelable.Creator<ManualSlot>() {
        public ManualSlot createFromParcel(Parcel in) {
            return new ManualSlot(in);
        }

        public ManualSlot[] newArray(int size) {
            return new ManualSlot[size];
        }
    };

    public final LocalTime fromTime;
    public final LocalTime toTime;

    public ManualSlot(Long id, LocalTime fromTime, LocalTime toTime, Long slot_id, LocalDate date, String ownerCellphone, boolean passwordProtected, HashSet<Client> reservations, ManualSlotBlueprint blueprint) {
        super(id, slot_id, date, ownerCellphone, passwordProtected, reservations, blueprint);
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public ManualSlot(LocalTime fromTime, LocalTime toTime, LocalDate date, String ownerCellphone, ManualSlotBlueprint blueprint) {
        this(null, fromTime, toTime, null, date, ownerCellphone, false, new HashSet<>(), blueprint);
    }

    public ManualSlot(JSONObject object, HashMap<Long, SlotBlueprint> blueprints) throws JSONException {
        super(object, blueprints);

        fromTime = LocalTime.parse(object.getString("from_time"));
        toTime = LocalTime.parse(object.getString("to_time"));
    }

    protected ManualSlot(Parcel in) {
        super(in);
        fromTime = (LocalTime) in.readSerializable();
        toTime = (LocalTime) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeSerializable(fromTime);
        parcel.writeSerializable(toTime);
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
