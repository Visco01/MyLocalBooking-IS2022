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

public class PeriodicSlot extends Slot implements ISelectableSlot {
    public static final Parcelable.Creator<PeriodicSlot> CREATOR
            = new Parcelable.Creator<PeriodicSlot>() {
        public PeriodicSlot createFromParcel(Parcel in) {
            return new PeriodicSlot(in);
        }

        public PeriodicSlot[] newArray(int size) {
            return new PeriodicSlot[size];
        }
    };

    public PeriodicSlot(Long id, Long slot_id, LocalDate date, String ownerCellphone, boolean passwordProtected, HashSet<Client> reservations, PeriodicSlotBlueprint blueprint) {
        super(id, slot_id, date, ownerCellphone, passwordProtected, reservations, blueprint);
    }

    public PeriodicSlot(LocalDate date, String ownerCellphone, PeriodicSlotBlueprint blueprint) {
        this(null, null, date, ownerCellphone, false, new HashSet<>(), blueprint);
    }

    public PeriodicSlot(JSONObject object, HashMap<Long, SlotBlueprint> blueprints) throws JSONException {
        super(object, blueprints);
    }

    protected PeriodicSlot(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
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
