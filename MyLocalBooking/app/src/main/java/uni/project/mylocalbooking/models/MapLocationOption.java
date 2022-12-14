package uni.project.mylocalbooking.models;


import com.google.maps.model.LatLng;

public class MapLocationOption {
    public final String address;
    public final Coordinates coordinates;
    public MapLocationOption(String address, LatLng location) {
        this.address = address;
        this.coordinates = new Coordinates(location.lat, location.lng);
    }
}
