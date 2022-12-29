package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;

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

    protected Establishment(Parcel in) {
        super(in);
        provider = in.readParcelable(Provider.class.getClassLoader());
        name = in.readString();
        address = in.readString();
        position = in.readParcelable(Coordinates.class.getClassLoader());
        placeId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(provider, i);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeParcelable(position, i);
        parcel.writeString(placeId);
    }
}
