package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;


import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;

public class Coordinates implements Parcelable {
    public static final Parcelable.Creator<Coordinates> CREATOR
            = new Parcelable.Creator<Coordinates>() {
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };

    public final double latitude;
    public final double longitude;

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public Coordinates(JSONObject object) throws JSONException {
        latitude = object.getDouble("lat");
        longitude = object.getDouble("lng");
    }

    protected Coordinates(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public com.google.maps.model.LatLng toApiCoordinates() {
        return new com.google.maps.model.LatLng(latitude, longitude);
    }

    public LatLng toMapsCoordinates() {
        return new LatLng(latitude, longitude);
    }
}
