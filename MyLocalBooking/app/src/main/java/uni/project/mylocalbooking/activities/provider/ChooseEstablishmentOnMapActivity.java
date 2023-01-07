package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.PorterDuff;
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


        AlternativeLocationListAdapter adapter = new AlternativeLocationListAdapter(option -> {
            viewModel.setTempPosition(option);
        });
        ListView optionsListView = findViewById(R.id.options_list);

        viewModel.getGeocodingResults().observe(this, mapLocationOptions -> {
            adapter.onListUpdated(mapLocationOptions);
            optionsListView.setVisibility(mapLocationOptions.isEmpty() ? View.GONE : View.VISIBLE);
        });

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
    }



}