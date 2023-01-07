package uni.project.mylocalbooking.activities.provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.MapsViewModel;
import uni.project.mylocalbooking.models.SelectableMapLocation;

public class ChooseEstablishmentOnMapActivity extends AppCompatActivity {
    private MapsViewModel viewModel;
    private List<SelectableMapLocation> geocodingResults;
    private AlternativeLocationListAdapter adapter;
    private ListView optionsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MapsViewModel.class);
        setContentView(R.layout.activity_choose_establishment_on_map);


        adapter = new AlternativeLocationListAdapter(option -> {
            viewModel.setTempPosition(option);
        });
        optionsListView = findViewById(R.id.options_list);

        viewModel.getGeocodingResults().observe(this, this::onNewGeocodingResults);

        viewModel.getSelectedLocation().observe(this, loc -> {
            boolean valid = loc != null;
            AppCompatButton confirmationButton = findViewById(R.id.confirmation_button);
            confirmationButton.setVisibility(valid ? View.VISIBLE : View.GONE);
            confirmationButton.setOnClickListener(btn -> {
                viewModel.confirmLocation(loc);
            });
        });
        optionsListView.setAdapter(adapter);

        viewModel.getConfirmedLocation().observe(this, loc -> finish());

        if(savedInstanceState != null) {
            geocodingResults = savedInstanceState.getParcelableArrayList("geocoding_results")
                    .stream().map(r -> (SelectableMapLocation)r).collect(Collectors.toList());
            onNewGeocodingResults(geocodingResults);
        }
    }

    private void onNewGeocodingResults(List<SelectableMapLocation> geocodingResults) {
        this.geocodingResults = geocodingResults;
        adapter.onListUpdated(geocodingResults);
        optionsListView.setVisibility(geocodingResults.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(geocodingResults != null) {
            SelectableMapLocation[] arr = new SelectableMapLocation[geocodingResults.size()];
            geocodingResults.toArray(arr);
            outState.putParcelableArray("geocoding_results", arr);
        }
    }
}