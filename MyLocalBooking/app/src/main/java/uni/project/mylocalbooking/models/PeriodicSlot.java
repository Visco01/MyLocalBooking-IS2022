package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class PeriodicSlot extends Slot implements IDatabaseSubclassModel, ISelectableSlot {
    public static final Parcelable.Creator<PeriodicSlot> CREATOR
            = new Parcelable.Creator<PeriodicSlot>() {
        public PeriodicSlot createFromParcel(Parcel in) {
            return new PeriodicSlot(in);
        }

        public PeriodicSlot[] newArray(int size) {
            return new PeriodicSlot[size];
        }
    };

    private Long id;

    public PeriodicSlot(Long id, Long slot_id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, PeriodicSlotBlueprint blueprint) {
        super(slot_id, date, owner, passwordProtected, reservations, blueprint);
        this.id = id;
        blueprint.addSlot(this);
    }

    public PeriodicSlot(LocalDate date, AppUser owner, PeriodicSlotBlueprint blueprint) {
        this(null, null, date, owner, false, new HashSet<>(), blueprint);
    }

    public PeriodicSlot(JSONObject object, HashMap<Long, SlotBlueprint> blueprints) throws JSONException {
        super(object, blueprints);

        id = object.getLong("subclass_id");
    }

    protected PeriodicSlot(Parcel in) {
        super(in);
        id = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(id);
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
        return ((PeriodicSlotBlueprint) super.blueprint).fromTime;
    }

    @Override
    public LocalTime getEnd() {
        return ((PeriodicSlotBlueprint) super.blueprint).toTime;
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
}
