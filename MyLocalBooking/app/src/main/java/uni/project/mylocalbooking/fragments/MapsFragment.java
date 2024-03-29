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
import uni.project.mylocalbooking.models.SelectableMapLocation;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private GoogleMap map;
    private MapsViewModel viewModel;

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setOnMapLongClickListener(this);
        viewModel = new ViewModelProvider(requireActivity()).get(MapsViewModel.class);

        viewModel.getGeocodingResults().observe(this, locations -> {
            SelectableMapLocation bestLocation = locations.get(0);
            LatLng position = bestLocation.coordinates.toMapsCoordinates();
            map.clear();
            map.addMarker(new MarkerOptions().position(position));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15), 700, null);
        });

        viewModel.getSelectedLocation().observe(this, location -> {
            LatLng coordinates = location.coordinates.toMapsCoordinates();
            map.clear();
            map.addMarker(new MarkerOptions().position(coordinates).alpha(1));
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
}