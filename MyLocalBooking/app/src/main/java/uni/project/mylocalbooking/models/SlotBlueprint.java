package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.function.IntFunction;


public abstract class SlotBlueprint extends DatabaseModel {
    public final Establishment establishment;
    public final HashSet<DayOfWeek> weekdays;
    public final Integer reservationLimit;
    public final LocalDate fromDate;
    public final LocalDate toDate;

    public Collection<Slot> slots;

    public SlotBlueprint(Long id, Establishment establishment, Integer reservationLimit, HashSet<DayOfWeek> weekdays, LocalDate fromDate, LocalDate toDate) {
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

    protected SlotBlueprint(Parcel in) {
        super(in);
        establishment = in.readParcelable(Establishment.class.getClassLoader());

        weekdays = new HashSet<>();
        int[] weekdaysArr = {};
        in.readIntArray(weekdaysArr);
        for(int w : weekdaysArr)
            weekdays.add(DayOfWeek.of(w));

        reservationLimit = in.readInt();
        fromDate = (LocalDate) in.readSerializable();
        toDate = (LocalDate) in.readSerializable();

        slots = new ArrayList<>();
        for(Parcelable slot : in.readParcelableArray(Slot.class.getClassLoader()))
            slots.add((Slot) slot);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);

        parcel.writeParcelable(establishment, i);

        List<Integer> wd = new ArrayList<>();
        for(DayOfWeek dow : weekdays)
            wd.add(dow.getValue());
        int[] wdArr = new int[wd.size()];
        for(int j = 0; j < wd.size(); j++)
            wdArr[j] = wd.get(j);
        parcel.writeIntArray(wdArr);

        parcel.writeInt(reservationLimit);
        parcel.writeSerializable(fromDate);
        parcel.writeSerializable(toDate);

        Parcelable[] slotsArr = {};
        slots.toArray(slotsArr);
        parcel.writeParcelableArray(slotsArr, i);
    }
}
