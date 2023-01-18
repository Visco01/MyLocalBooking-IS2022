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
import java.util.LinkedHashSet;
import java.util.List;


public abstract class SlotBlueprint extends DatabaseSubclassModel {
    public static SlotBlueprint fromJson(JSONObject object, Establishment establishment) throws JSONException {
        String clientType = object.getString("type");
        if(clientType.equals("periodic"))
            return new PeriodicSlotBlueprint(object, establishment);

        if (clientType.equals("manual"))
            return new ManualSlotBlueprint(object, establishment);

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

    public final HashMap<LocalDate, LinkedHashSet<Slot>> slots = new HashMap<>();

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

    protected SlotBlueprint(JSONObject object, Establishment establishment) throws JSONException {
        super(object);
        weekdays = getDaysOfWeek(object.getInt("weekdays"));
        reservationLimit = object.has("reservation_limit") && !object.isNull("reservation_limit") ?
                object.getInt("reservation_limit") : null;

        fromDate = LocalDate.parse(object.getString("from_date"));
        toDate = LocalDate.parse(object.getString("to_date"));
        this.establishment = establishment;
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
                slots.put(slot.date, new LinkedHashSet<>());
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
            result = result | 1 << 7 - (int) w.getValue();
        return result;
    }

    public boolean overlapsWith(SlotBlueprint other) {
        if(!establishment.equals(other.establishment))
            return false;

        if(!fromDate.isBefore(other.toDate) || !other.fromDate.isBefore(fromDate))
            return false;

        HashSet<DayOfWeek> intersection = new HashSet<>();
        intersection.addAll(weekdays);
        intersection.retainAll(other.weekdays);

        return !intersection.isEmpty();
    }

    /**
     * This implementation must be called by subclasses overriding this method
     * @param slot
     */
    public void addSlot(@NotNull Slot slot) {
        LinkedHashSet<Slot> slotsInDate = slots.get(slot.date);
        if(slotsInDate == null) {
            slotsInDate = new LinkedHashSet<>();
            slots.put(slot.date, slotsInDate);
        }
        slotsInDate.add(slot);
    }
    public abstract void invalidateReservations(LocalDate date);
}
