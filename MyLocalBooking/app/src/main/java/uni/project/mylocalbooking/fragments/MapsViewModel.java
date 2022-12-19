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
import uni.project.mylocalbooking.models.SelectableMapLocation;

public class MapsViewModel  extends ViewModel {
    private final MutableLiveData<SelectableMapLocation> selectedPosition = new MutableLiveData<>();
    private final MutableLiveData<List<SelectableMapLocation>> geocodingResults = new MutableLiveData<>();
    private final MutableLiveData<SelectableMapLocation> tempMarkerPosition = new MutableLiveData<>();
    public final MutableLiveData<SelectableMapLocation> selectedLocation = new MutableLiveData<>();
    public final MutableLiveData<SelectableMapLocation> confirmedLocation = new MutableLiveData<>();

    public LiveData<SelectableMapLocation> getSelectedPosition() {
        return selectedPosition;
    }
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
        GeocodingApi.reverseGeocode(MyLocalBooking.geoApiContext, toApiCoordinates(position)).setCallback(new PendingResult.Callback<GeocodingResult[]>() {
            @Override
            public void onResult(GeocodingResult[] results) {
                List<SelectableMapLocation> options = new ArrayList<>();
                for(GeocodingResult result : results)
                    options.add(new SelectableMapLocation(result));

                geocodingResults.postValue(options);
                selectedPosition.postValue(options.get(0));
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    LiveData<SelectableMapLocation> getTempPosition() {
        return tempMarkerPosition;
    }

    public void setTempPosition(SelectableMapLocation pos) {
        tempMarkerPosition.setValue(pos);
    }

    public LiveData<SelectableMapLocation> getSelectedLocation() {
        return selectedLocation;
    }

    void setSelectedLocation(SelectableMapLocation location) {
        selectedLocation.setValue(location);
    }

    public static com.google.maps.model.LatLng toApiCoordinates(LatLng loc) {
        return new com.google.maps.model.LatLng(loc.latitude, loc.longitude);
    }
}
