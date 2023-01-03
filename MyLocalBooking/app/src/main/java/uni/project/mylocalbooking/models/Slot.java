package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import uni.project.mylocalbooking.api.IMyLocalBookingAPI;

public abstract class Slot extends DatabaseModel {
    public static Slot fromJson(JSONObject object, HashMap<Long, SlotBlueprint> blueprints) throws JSONException {
        String clientType = object.getString("type");
        if(clientType.equals("periodic"))
            return new PeriodicSlot(object, blueprints);

        if (clientType.equals("manual"))
            return new ManualSlot(object, blueprints);

        throw new IllegalArgumentException();
    }

    public final LocalDate date;
    public boolean passwordProtected;
    private AppUser owner;
    public HashSet<Client> reservations;
    public SlotBlueprint blueprint;

    private String ownerCellphone;

    public Slot(Long id, LocalDate date, AppUser owner, boolean passwordProtected, HashSet<Client> reservations, @NotNull SlotBlueprint blueprint) {
        super(id);
        this.date = date;
        this.owner = owner;
        this.passwordProtected = passwordProtected;
        this.reservations = reservations;
        this.blueprint = blueprint;

        blueprint.addSlot(this);
    }

    public Slot(LocalDate date, AppUser owner, SlotBlueprint blueprint) {
        this(null, date, owner, false, new HashSet<>(), blueprint);
    }

    protected Slot(JSONObject object, HashMap<Long, SlotBlueprint> blueprints) throws JSONException {
        super(object);
        blueprint = blueprints.get(object.getLong("blueprint_subclass_id"));
        date = LocalDate.parse(object.getString("date"));
        passwordProtected = !object.getString("password_digest").isEmpty();
        ownerCellphone = object.getString("owner_cellphone");
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

    public void setOwner(AppUser user) {
        owner = user;
        ownerCellphone = user.cellphone;
    }

    public AppUser getOwner() {
        if(owner != null)
            return owner;

        return IMyLocalBookingAPI.getApiInstance().getUserByCellphone(ownerCellphone);
    }

    public boolean isOwner(AppUser user) {
        return ownerCellphone.equals(user.cellphone);
    }
}
