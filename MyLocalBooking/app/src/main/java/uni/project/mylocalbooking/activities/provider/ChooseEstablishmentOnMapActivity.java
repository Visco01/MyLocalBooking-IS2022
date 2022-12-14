package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.MapsViewModel;

public class ChooseEstablishmentOnMapActivity extends AppCompatActivity {
    MapsViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MapsViewModel.class);
        setContentView(R.layout.activity_choose_establishment_on_map);
    }
}