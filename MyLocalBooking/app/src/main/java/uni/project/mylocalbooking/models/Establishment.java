package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uni.project.mylocalbooking.api.IMyLocalBookingAPI;

public class Establishment extends DatabaseModel {
    public static class PartialReservationsResultsException extends Exception {}

    public static final Parcelable.Creator<Establishment> CREATOR
            = new Parcelable.Creator<Establishment>() {
        public Establishment createFromParcel(Parcel in) {
            return new Establishment(in);
        }

        public Establishment[] newArray(int size) {
            return new Establishment[size];
        }
    };

    private String providerCellphone;
    private Provider provider;
    public final String name;
    public final String address;
    public final Coordinates position;
    public final String placeId;

    public Collection<SlotBlueprint> blueprints;

    public Establishment(Long id, Provider provider, String name, String address, Coordinates position, String placeId) {
        super(id);
        setProvider(provider);
        this.name = name;
        this.address = address;
        this.position = position;
        this.placeId = placeId;
    }

    public Establishment(Provider provider, String name, String address, Coordinates position, String placeId) {
        this(null, provider, name, address, position, placeId);
    }

    public Establishment(JSONObject object) throws JSONException {
        super(object.getLong("id"));

        providerCellphone = object.getString("provider_cellphone");
        name = object.getString("name");
        address = object.getString("address");
        position = new Coordinates(object.getJSONObject("coordinates"));
        placeId = object.getString("place_id");

        JSONArray blueprintsArr = object.getJSONArray("slot_blueprints");
        for(int i = 0; i < blueprintsArr.length(); i++)
            this.addBlueprint(SlotBlueprint.fromJson(blueprintsArr.getJSONObject(i)));
    }

    protected Establishment(Parcel in) {
        super(in);
        provider = in.readParcelable(Provider.class.getClassLoader());
        name = in.readString();
        address = in.readString();
        position = in.readParcelable(Coordinates.class.getClassLoader());
        placeId = in.readString();

        blueprints = new ArrayList<>();
        for(Parcelable b : in.readParcelableArray(SlotBlueprint.class.getClassLoader())) {
            SlotBlueprint blueprint = (SlotBlueprint) b;
            blueprints.add(blueprint);
            blueprint.establishment = this;
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(provider, i);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeParcelable(position, i);
        parcel.writeString(placeId);

        SlotBlueprint[] blueprintsArr = new SlotBlueprint[blueprints.size()];
        blueprints.toArray(blueprintsArr);
        parcel.writeParcelableArray(blueprintsArr, i);
    }

    public Collection<SlotBlueprint> getBlueprints(LocalDate date) throws PartialReservationsResultsException {
        Stream<SlotBlueprint> activeBlueprintsInDate = blueprints.stream().filter(b ->
                b.fromDate.compareTo(date) <= 0 && b.toDate.compareTo(date) > 0 &&
                        b.weekdays.contains(date.getDayOfWeek()));

        List<SlotBlueprint> results = activeBlueprintsInDate.collect(Collectors.toList());
        if(results.isEmpty())
            return results;

        boolean completeResults = results.stream().allMatch(blueprint -> blueprint.slots.containsKey(date)) ||
                IMyLocalBookingAPI.getApiInstance().getReservations(this, date);

        if(!completeResults)
            throw new PartialReservationsResultsException();

        return activeBlueprintsInDate.collect(Collectors.toList());
    }

    public Provider getProvider() {
        if(provider != null)
            return provider;

        provider = (Provider) IMyLocalBookingAPI.getApiInstance().getUserByCellphone(providerCellphone);
        return provider;
    }

    protected void setProvider(Provider provider) {
        this.provider = provider;
        this.providerCellphone = provider != null ? provider.cellphone : null;
    }

    protected void addBlueprint(SlotBlueprint blueprint) {
        this.blueprints.add(blueprint);
    }
}
