package uni.project.mylocalbooking.fragments;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeocodingApi;
import com.google.maps.PendingResult;
import com.google.maps.android.SphericalUtil;
import com.google.maps.model.GeocodingResult;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.models.SelectableMapLocation;

public class MapsViewModel  extends ViewModel {
    private final MutableLiveData<List<SelectableMapLocation>> geocodingResults = new MutableLiveData<>();
    private final MutableLiveData<SelectableMapLocation> selectedLocation = new MutableLiveData<>();
    private final MutableLiveData<SelectableMapLocation> confirmedLocation = new MutableLiveData<>();

    public LiveData<List<SelectableMapLocation>> getGeocodingResults() {
        return geocodingResults;
    }

    public void confirmLocation(SelectableMapLocation loc) {
        confirmedLocation.setValue(loc);
    }

    public LiveData<SelectableMapLocation> getConfirmedLocation() {
        return confirmedLocation;
    }

    void setPosition(LatLng position) {
        LatLng[] bounds = new LatLng[2];
        createBoundsAroundCenter(position, 100, bounds);
        GeocodingApi.reverseGeocode(MyLocalBooking.geoApiContext, toApiCoordinates(position))
                .bounds(convertCoordinates(bounds[0]), convertCoordinates(bounds[1]))
                .setCallback(new PendingResult.Callback<GeocodingResult[]>() {
            @Override
            public void onResult(GeocodingResult[] results) {
                List<SelectableMapLocation> options = new ArrayList<>();
                for(GeocodingResult result : results) {
                    float[] r = new float[3];
                    Location.distanceBetween(position.latitude, position.longitude, result.geometry.location.lat, result.geometry.location.lng, r);
                    if(r[0] < 100) {
                        options.add(new SelectableMapLocation(result));
                    }
                }
                geocodingResults.postValue(options);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    private com.google.maps.model.LatLng convertCoordinates(LatLng coordinates) {
        return new com.google.maps.model.LatLng(coordinates.latitude, coordinates.longitude);
    }

    private LatLng convertCoordinates(com.google.maps.model.LatLng coordinates) {
        return new LatLng(coordinates.lat, coordinates.lng);
    }

    private void createBoundsAroundCenter(LatLng center, double radiusInMeters, LatLng[] results) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);

        results[0] = southwestCorner;
        results[1] = northeastCorner;
    }

    public LiveData<SelectableMapLocation> getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(SelectableMapLocation location) {
        selectedLocation.setValue(location);
    }

    public static com.google.maps.model.LatLng toApiCoordinates(LatLng loc) {
        return new com.google.maps.model.LatLng(loc.latitude, loc.longitude);
    }
}
