package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Slot extends DatabaseModel {
    public final LocalDate date;
    public boolean passwordProtected;
    public AppUser owner;
    public HashSet<Client> reservations;
    public SlotBlueprint blueprint;

    public Slot(Long id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, @NotNull SlotBlueprint blueprint) {
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

    protected Slot(JSONObject object, HashMap<Long, SlotBlueprint> blueprints) throws JSONException {
        super(object);
        blueprint = blueprints.get(object.getLong("blueprint_subclass_id"));
        date = LocalDate.parse(object.getString("date"));
        passwordProtected = !object.getString("password_digest").isEmpty();
        owner = AppUser.fromJson(object.getJSONObject("owner"));
        reservations = new HashSet<>();

        JSONArray reservationsArr = object.getJSONArray("reservations");
        for(int i = 0; i < reservationsArr.length(); i++)
            reservations.add(new Client(reservationsArr.getJSONObject(i)));
    }

    protected Slot(Parcel in) {
        super(in);
        date = (LocalDate) in.readSerializable();
        owner = in.readParcelable(AppUser.class.getClassLoader());
        passwordProtected = in.readByte() != 0;

        reservations = new HashSet<>();
        for(Parcelable client : in.readParcelableArray(Client.class.getClassLoader()))
            reservations.add((Client) client);
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
    }
}
