package uni.project.mylocalbooking.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeocodingApi;
import com.google.maps.PendingResult;
import com.google.maps.model.GeocodingResult;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.MapLocationOption;
import uni.project.mylocalbooking.models.Provider;

public class MapsViewModel  extends ViewModel {
    private MutableLiveData<Coordinates> selectedPosition = new MutableLiveData<>();
    private MutableLiveData<List<MapLocationOption>> geocodingResults = new MutableLiveData<>();

    public LiveData<Coordinates> getSelectedPosition() {
        return selectedPosition;
    }

    public LiveData<List<MapLocationOption>> getGeocodingResults() {
        return geocodingResults;
    }

    void setPosition(LatLng position) {
        Coordinates pos = new Coordinates(position.latitude, position.longitude);
        GeocodingApi.reverseGeocode(MyLocalBooking.geoApiContext, convertCoordinates(position)).setCallback(new PendingResult.Callback<GeocodingResult[]>() {
            @Override
            public void onResult(GeocodingResult[] results) {
                selectedPosition.postValue(pos);
                List<MapLocationOption> options = new ArrayList<>();
                for(GeocodingResult result : results)
                    options.add(new MapLocationOption(result.formattedAddress, result.geometry.location));
                geocodingResults.postValue(options);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    private LatLng convertCoordinates(com.google.maps.model.LatLng loc) {
        return new LatLng(loc.lat, loc.lng);
    }

    private com.google.maps.model.LatLng convertCoordinates(LatLng loc) {
        return new com.google.maps.model.LatLng(loc.latitude, loc.longitude);
    }
}
