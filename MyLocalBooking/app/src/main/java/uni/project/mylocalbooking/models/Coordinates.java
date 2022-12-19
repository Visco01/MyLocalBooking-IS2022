package uni.project.mylocalbooking.models;

import com.google.android.gms.maps.model.LatLng;

public class Coordinates {
    public final double latitude;
    public final double longitude;

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public com.google.maps.model.LatLng toApiCoordinates() {
        return new com.google.maps.model.LatLng(latitude, longitude);
    }

    public LatLng toMapsCoordinates() {
        return new LatLng(latitude, longitude);
    }
}
