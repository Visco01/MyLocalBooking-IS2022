package uni.project.mylocalbooking.models;

import com.google.maps.model.GeocodingResult;

public class SelectableMapLocation {
    public final String address;
    public final Coordinates coordinates;
    public SelectableMapLocation(GeocodingResult result) {
        address = result.formattedAddress;
        this.coordinates = new Coordinates(result.geometry.location.lat, result.geometry.location.lng);
    }
}
