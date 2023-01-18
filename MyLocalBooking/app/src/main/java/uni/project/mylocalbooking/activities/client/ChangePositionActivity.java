package uni.project.mylocalbooking.activities.client;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.provider.ChooseEstablishmentOnMapActivity;
import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.SelectableMapLocation;

public class ChangePositionActivity extends AppCompatActivity {

    private float lat, lon;
    private String mapAddress;
    private TextView pos;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_position);

        FloatingActionButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), ProfileClientActivity.class));
            }
        });

        findViewById(R.id.choose_on_map_button).setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseEstablishmentOnMapActivity.class);
            activityResultLauncher.launch(intent);
        });

        pos = findViewById(R.id.positionTextView);

        Button confirmButton = findViewById(R.id.confirm_change_position);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos.getText().toString().isEmpty()){
                    failedChange();
                }
                else{
                    Coordinates coords = new Coordinates(lat, lon);
                    IMyLocalBookingAPI api = IMyLocalBookingAPI.getApiInstance();
                    //Sempre stesso errore sul parsing!!!
                    api.setPreferredPosition(
                            coords,
                            (a) -> confirmChange(),
                            (b) -> {
                                        System.out.println("Failed Change Pos");
                                        failedChange2();
                                    }
                    );
                }

            }
        });

    }
    
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    SelectableMapLocation location = (SelectableMapLocation) data.getExtras().getParcelable("selected_location");
                    lat = (float) location.coordinates.latitude;
                    lon = (float) location.coordinates.longitude;
                    mapAddress = location.address;
                    pos.setText(location.address);
                }
            });


    private void confirmChange() {
        DialogFragment newFragment = new SuccessFragment("Position Changed Successfully",
                "You position was changed successfully, remember that the available " +
                        "establishments could have changed!");
        newFragment.show(getSupportFragmentManager(), "successAddEstablishment");
    }

    private void failedChange(){
        DialogFragment newFragment = new FailureFragment("Position change failed",
                "You can't leave the field empty. Please choose a position clicking the" +
                        "button below!");
        newFragment.show(getSupportFragmentManager(), "failedAddEstablishment");
    }

    private void failedChange2(){
        DialogFragment newFragment = new FailureFragment("API Position change failed",
                "An unexpected error happened, please try again");
        newFragment.show(getSupportFragmentManager(), "failedAddEstablishment");
    }
}