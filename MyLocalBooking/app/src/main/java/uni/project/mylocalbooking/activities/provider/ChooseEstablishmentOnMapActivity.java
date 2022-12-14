package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.MapsViewModel;

public class ChooseEstablishmentOnMapActivity extends AppCompatActivity {
    MapsViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MapsViewModel.class);
        setContentView(R.layout.activity_choose_establishment_on_map);
        MapLocationOptionListAdapter adapter = new MapLocationOptionListAdapter();
        ListView optionsListView = findViewById(R.id.options_list);

        viewModel.getGeocodingResults().observe(this, mapLocationOptions -> {
            adapter.onListUpdated(mapLocationOptions);

            optionsListView.setVisibility(mapLocationOptions.isEmpty() ? View.GONE : View.VISIBLE);
        });
        optionsListView.setAdapter(adapter);
    }
}