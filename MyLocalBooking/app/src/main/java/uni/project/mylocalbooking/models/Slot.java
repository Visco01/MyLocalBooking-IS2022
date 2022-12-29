package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.HashSet;

public abstract class Slot extends DatabaseModel {
    public final LocalDate date;
    public boolean passwordProtected;
    public AppUser owner;
    public HashSet<Client> reservations;
    public final SlotBlueprint blueprint;

    public Slot(Long id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, SlotBlueprint blueprint) {
        super(id);
        this.date = date;
        this.owner = owner;
        this.passwordProtected = passwordProtected;
        this.reservations = reservations;
        this.blueprint = blueprint;

        if (blueprint.slots == null)
            blueprint.slots = new ArrayList<>();
        blueprint.slots.add(this);
    }

    public Slot(LocalDate date, AppUser owner, SlotBlueprint blueprint) {
        this(null, date, owner, false, new HashSet<>(), blueprint);
    }

    protected Slot(Parcel in) {
        super(in);
        date = (LocalDate) in.readSerializable();
        owner = in.readParcelable(AppUser.class.getClassLoader());
        passwordProtected = in.readByte() != 0;

        reservations = new HashSet<>();
        for(Parcelable client : in.readParcelableArray(Client.class.getClassLoader()))
            reservations.add((Client) client);

        blueprint = in.readParcelable(SlotBlueprint.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeSerializable(date);
        parcel.writeParcelable(owner, i);
        parcel.writeByte((byte) (passwordProtected ? 1 : 0));

        Client[] reservationsArr = new Client[reservations.size()];
        reservations.toArray(reservationsArr);
        parcel.writeParcelableArray(reservationsArr, i);

        parcel.writeParcelable(blueprint, i);
    }
}
