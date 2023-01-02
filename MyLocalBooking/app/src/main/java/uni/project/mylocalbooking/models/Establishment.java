package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

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
    public static final Parcelable.Creator<Establishment> CREATOR
            = new Parcelable.Creator<Establishment>() {
        public Establishment createFromParcel(Parcel in) {
            return new Establishment(in);
        }

        public Establishment[] newArray(int size) {
            return new Establishment[size];
        }
    };

    public final Provider provider;
    public final String name;
    public final String address;
    public final Coordinates position;
    public final String placeId;

    public Collection<SlotBlueprint> blueprints;

    public Establishment(Long id, Provider provider, String name, String address, Coordinates position, String placeId) {
        super(id);
        this.provider = provider;
        this.name = name;
        this.address = address;
        this.position = position;
        this.placeId = placeId;
    }

    public Establishment(Long id, String name, String address, Coordinates position, String placeId) {
        super(id);
        this.placeId = placeId;
        this.provider = null;
        this.name = name;
        this.address = address;
        this.position = position;
    }

    public Establishment(Provider provider, String name, String address, Coordinates position, String placeId) {
        this(null, provider, name, address, position, placeId);
    }

    public Establishment(JSONObject object) throws JSONException {
        super(object.getLong("id"));

        provider = new Provider(object.getJSONObject("provider"));
        name = object.getString("name");
        address = object.getString("address");
        position = new Coordinates(object.getJSONObject("coordinates"));
        placeId = object.getString("place_id");
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

    public List<SlotBlueprint> getBlueprints(LocalDate date) {
        Stream<SlotBlueprint> activeBlueprintsInDate = blueprints.stream().filter(b ->
                b.fromDate.compareTo(date) <= 0 && b.toDate.compareTo(date) > 0 &&
                        b.weekdays.contains(date.getDayOfWeek()));

        Optional<SlotBlueprint> someBlueprint = activeBlueprintsInDate.findAny();
        if(!someBlueprint.isPresent())
            return new ArrayList<>();

        // there are no partial results for a given date, so I can just check any blueprint
        boolean completeResults = someBlueprint.get().slots.containsKey(date) ||
                IMyLocalBookingAPI.getApiInstance().getReservations(this, date);

        return completeResults ? activeBlueprintsInDate.collect(Collectors.toList()) : null;
    }

    protected void addBlueprint(SlotBlueprint blueprint) {
        this.blueprints.add(blueprint);
    }
}
