package uni.project.mylocalbooking.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.SelectableMapLocation;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap map;
    private MapsViewModel viewModel;

    private Marker alternativeMarker;
    private Marker placedMarker;
    private Marker selectedMarker;
    private SelectableMapLocation placedLocation;
    private SelectableMapLocation alternativeLocation;

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);
        viewModel = new ViewModelProvider(requireActivity()).get(MapsViewModel.class);

        viewModel.getSelectedPosition().observe(this, location -> {
            LatLng position = location.coordinates.toMapsCoordinates();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15), 700, null);
            placedMarker = map.addMarker(new MarkerOptions().position(position));
            selectedMarker = placedMarker;
            placedLocation = location;
            toggleSelectedMarker(placedMarker);
        });

        viewModel.getTempPosition().observe(this, location -> {
            if(alternativeMarker != null)
                alternativeMarker.remove();

            LatLng coordinates = location.coordinates.toMapsCoordinates();
            alternativeMarker = map.addMarker(new MarkerOptions().position(coordinates).alpha(0.5f));
            alternativeLocation = location;
            toggleSelectedMarker(placedMarker);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15), 700, null);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        map.clear();
        viewModel.setPosition(latLng);
    }

    private void toggleSelectedMarker(Marker marker) {
        selectedMarker = marker;
        marker.setAlpha(1);
        if(marker == placedMarker) {
            if(alternativeMarker != null)
                alternativeMarker.setAlpha(0.5f);
            viewModel.setSelectedLocation(placedLocation);
        }
        else {
            placedMarker.setAlpha(0.5f);
            viewModel.setSelectedLocation(alternativeLocation);
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if(marker.getPosition().equals(placedMarker.getPosition()))
            toggleSelectedMarker(placedMarker);
        else
            toggleSelectedMarker(alternativeMarker);

        return true;
    }
}