package uni.project.mylocalbooking.activities.provider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Provider;
import uni.project.mylocalbooking.models.SelectableMapLocation;

public class AddEstablishmentActivity extends AppCompatActivity {

    private FloatingActionButton backButton;
    private Button confirmButton;
    private boolean success = false;
    private float lat, lon;
    private static long id = 100; //100 Default value
    private String pId;
    private String mapAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_establishment);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), HomeProviderActivity.class));
            }
        });

        EditText name = findViewById(R.id.newEstName);
        TextView pos = findViewById(R.id.newEstAddress);


        confirmButton = findViewById(R.id.confirm_add_est);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputName = name.getText().toString();
                String inputPos = pos.getText().toString();
                // Checks
                if (inputName.length() < 3 || inputPos.length() < 3){
                    success = false;
                    failedAdd();
                }
                else if (lat == 0.0f || lon == 0.0f){
                    success = false;
                    failedAddPos();
                }
                else{
                    success = true;
                }

                if (success){
                    System.out.println(mapAddress + "\n");
                    IMyLocalBookingAPI api = IMyLocalBookingAPI.getApiInstance();
                    api.addEstablishment(new Establishment(
                            (Provider) MyLocalBooking.getCurrentUser(),
                            inputName, mapAddress,
                            new Coordinates(lat, lon),
                            pId),
                            (a) -> confirmAdd(),
                            (b) -> {
                                        System.out.println("Data elaboration error");
                                        failedAdd();
                                    }
                    );
                }

            }
        });


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        SelectableMapLocation location = (SelectableMapLocation) data.getExtras().getParcelable("selected_location");
                        lat = (float) location.coordinates.latitude;
                        lon = (float) location.coordinates.longitude;
                        mapAddress = location.address;
                        pId = location.placeId;
                        pos.setText(location.address);
                    }
                });

        findViewById(R.id.choose_on_map_button).setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseEstablishmentOnMapActivity.class);
            activityResultLauncher.launch(intent);
        });
    }

    private void confirmAdd() {
        DialogFragment newFragment = new SuccessFragment("Added establishment successfully",
                "Your establishment was successfully added to the database, you can check it in 'My Establishment'");
        newFragment.show(getSupportFragmentManager(), "successAddEstablishment");
    }

    private void failedAdd(){
        DialogFragment newFragment = new FailureFragment("Added establishment failed",
                "At least one of the fields is not valid, please try again with a different input");
        newFragment.show(getSupportFragmentManager(), "failedAddEstablishment");
    }

    private void failedAddPos(){
        DialogFragment newFragment = new FailureFragment("Added establishment failed",
                "Please pick a position on the map. To do so, click on the green button up here" +
                        " and then tap for a few seconds in your position on the map");
        newFragment.show(getSupportFragmentManager(), "failedAddEstablishment");
    }

    private static String nextId(){
        id++;
        return Long.toString(id);
    }

}