package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.function.IntFunction;


public abstract class SlotBlueprint extends DatabaseModel {
    private static HashSet<DayOfWeek> getDaysOfWeek(int weekDays){
        HashSet<DayOfWeek> map = new HashSet<>();
        for(int i = 0; i < 7; i++){
            if(((int)Math.pow(2, i) & weekDays) != 0)
                map.add(DayOfWeek.of(7 - i));
        }
        return map;
    }

    public Establishment establishment;
    public final HashSet<DayOfWeek> weekdays;
    public final Integer reservationLimit;
    public final LocalDate fromDate;
    public final LocalDate toDate;

    public Collection<Slot> slots = new ArrayList<>();

    public SlotBlueprint(Long id, @NotNull Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        super(id);
        this.weekdays = weekdays;
        this.reservationLimit = reservationLimit;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.establishment = establishment;

        if(establishment.blueprints == null)
            establishment.blueprints = new ArrayList<>();
        establishment.blueprints.add(this);
    }

    public SlotBlueprint(Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        this(null, establishment, reservationLimit, weekdays, fromDate, toDate);
    }

    protected SlotBlueprint(JSONObject object) throws JSONException {
        super(object);
        weekdays = getDaysOfWeek(object.getInt("weekdays"));
        reservationLimit = object.has("reservation_limit") ? object.getInt("reservation_limit") : null;
        fromDate = LocalDate.parse(object.getString("from_date"));
        toDate = LocalDate.parse(object.getString("to_date"));
    }

    protected SlotBlueprint(Parcel in) {
        super(in);

        weekdays = new HashSet<>();
        int[] weekdaysArr = new int[in.readInt()];
        in.readIntArray(weekdaysArr);
        for(int w : weekdaysArr)
            weekdays.add(DayOfWeek.of(w));

        int reservationLimit = in.readInt();
        this.reservationLimit = reservationLimit == 0 ? null : reservationLimit;

        fromDate = (LocalDate) in.readSerializable();
        toDate = (LocalDate) in.readSerializable();

        for(Parcelable s : in.readParcelableArray(Slot.class.getClassLoader())) {
            Slot slot = (Slot) s;
            slots.add(slot);
            slot.blueprint = this;
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);

        parcel.writeInt(weekdays.size());
        List<Integer> wd = new ArrayList<>();
        for(DayOfWeek dow : weekdays)
            wd.add(dow.getValue());
        int[] wdArr = new int[wd.size()];
        for(int j = 0; j < wd.size(); j++)
            wdArr[j] = wd.get(j);
        parcel.writeIntArray(wdArr);

        parcel.writeInt(reservationLimit == null ? 0 : reservationLimit);
        parcel.writeSerializable(fromDate);
        parcel.writeSerializable(toDate);

        Slot[] slotsArr = new Slot[slots.size()];
        slots.toArray(slotsArr);
        parcel.writeParcelableArray(slotsArr, i);
    }

    protected void addSlot(Slot slot) {
        slots.add(slot);
    }
}
