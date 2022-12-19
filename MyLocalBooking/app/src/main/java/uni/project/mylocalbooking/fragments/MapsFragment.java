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

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private GoogleMap map;
    private MapsViewModel viewModel;

    private Marker alternativeMarker;
    private Marker placedMarker;
    private Marker selectedMarker;

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(marker -> {
            if(marker.getPosition().equals(alternativeMarker.getPosition()))
                toggleSelectedMarker(alternativeMarker);
            else
                toggleSelectedMarker(placedMarker);

            return true;
        });
        viewModel = new ViewModelProvider(requireActivity()).get(MapsViewModel.class);

        viewModel.getSelectedPosition().observe(this, latLng -> {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 700, null);
        });

        viewModel.getTempPosition().observe(this, pos -> {
            if(alternativeMarker != null)
                alternativeMarker.remove();

            alternativeMarker = map.addMarker(new MarkerOptions().position(pos).alpha(0.5f));
            toggleSelectedMarker(placedMarker);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15), 700, null);
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
        placedMarker = map.addMarker(new MarkerOptions().position(latLng));
        selectedMarker = placedMarker;
        viewModel.setPosition(latLng);
    }

    private void toggleSelectedMarker(Marker marker) {
        selectedMarker = marker;
        marker.setAlpha(1);
        if(marker == placedMarker)
            alternativeMarker.setAlpha(0.5f);
        else
            placedMarker.setAlpha(0.5f);
    }
}