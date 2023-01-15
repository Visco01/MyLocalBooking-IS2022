package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public abstract class SlotBlueprint extends DatabaseSubclassModel {
    public static SlotBlueprint fromJson(JSONObject object) throws JSONException {
        String clientType = object.getString("type");
        if(clientType.equals("periodic"))
            return new PeriodicSlotBlueprint(object);

        if (clientType.equals("manual"))
            return new ManualSlotBlueprint(object);

        throw new IllegalArgumentException();
    }

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

    private final HashMap<LocalDate, List<Slot>> slots = new HashMap<>();

    public SlotBlueprint(Long sublassId, Long superclassId, @NotNull Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        super(sublassId, superclassId);
        this.weekdays = weekdays;
        this.reservationLimit = reservationLimit;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.establishment = establishment;

        establishment.addBlueprint(this);
    }

    public SlotBlueprint(Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
        this(null, null, establishment, reservationLimit, weekdays, fromDate, toDate);
    }

    protected SlotBlueprint(JSONObject object) throws JSONException {
        super(object);
        weekdays = getDaysOfWeek(object.getInt("weekdays"));
        reservationLimit = object.has("reservation_limit") && !object.isNull("reservation_limit") ?
                object.getInt("reservation_limit") : null;

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
            if(!slots.containsKey(slot.date))
                slots.put(slot.date, new ArrayList<>());
            slots.get(slot.date).add(slot);
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

        Slot[] slotsArr = slots.values().stream().flatMap(Collection::stream).toArray(Slot[]::new);
        parcel.writeParcelableArray(slotsArr, i);
    }

    public int getDaysOfWeekAsInt(){
        int result = 0;
        for (DayOfWeek w : weekdays)
            result = result | 7 - w.getValue();
        return result;
    }

    public abstract void addSlot(@NotNull Slot slot);
    public abstract boolean hasSlotsInDate(@NotNull LocalDate date);
}
