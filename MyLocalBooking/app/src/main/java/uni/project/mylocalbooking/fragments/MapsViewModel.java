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
import uni.project.mylocalbooking.models.AlternativeLocation;

public class MapsViewModel  extends ViewModel {
    private final MutableLiveData<LatLng> selectedPosition = new MutableLiveData<>();
    private final MutableLiveData<List<AlternativeLocation>> geocodingResults = new MutableLiveData<>();
    private final MutableLiveData<LatLng> tempMarkerPosition = new MutableLiveData<>();

    public LiveData<LatLng> getSelectedPosition() {
        return selectedPosition;
    }

    public LiveData<List<AlternativeLocation>> getGeocodingResults() {
        return geocodingResults;
    }

    void setPosition(LatLng position) {
        LatLng pos = new LatLng(position.latitude, position.longitude);
        GeocodingApi.reverseGeocode(MyLocalBooking.geoApiContext, convertCoordinates(position)).setCallback(new PendingResult.Callback<GeocodingResult[]>() {
            @Override
            public void onResult(GeocodingResult[] results) {
                selectedPosition.postValue(pos);
                List<AlternativeLocation> options = new ArrayList<>();
                for(int i = 1; i < results.length; i++) {
                    GeocodingResult result = results[i];
                    options.add(new AlternativeLocation(result.formattedAddress, result.geometry.location));
                }
                geocodingResults.postValue(options);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    LiveData<LatLng> getTempPosition() {
        return tempMarkerPosition;
    }

    public void setTempPosition(LatLng pos) {
        tempMarkerPosition.setValue(pos);
    }

    public static LatLng convertCoordinates(com.google.maps.model.LatLng loc) {
        return new LatLng(loc.lat, loc.lng);
    }

    public static com.google.maps.model.LatLng convertCoordinates(LatLng loc) {
        return new com.google.maps.model.LatLng(loc.latitude, loc.longitude);
    }
}
