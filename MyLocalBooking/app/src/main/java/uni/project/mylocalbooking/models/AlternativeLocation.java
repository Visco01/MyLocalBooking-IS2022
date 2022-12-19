package uni.project.mylocalbooking.models;


import com.google.maps.model.LatLng;

public class AlternativeLocation {
    public final String address;
    public final Coordinates coordinates;
    public AlternativeLocation(String address, LatLng location) {
        this.address = address;
        this.coordinates = new Coordinates(location.lat, location.lng);
    }
}
