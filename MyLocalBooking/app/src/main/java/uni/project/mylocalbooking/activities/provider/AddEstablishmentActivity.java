package uni.project.mylocalbooking.activities.provider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    // TODO: position picker

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

        confirmButton = findViewById(R.id.confirm_add_est);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = findViewById(R.id.newEstName);
                EditText pos = findViewById(R.id.newEstAddress);
                String inputName = name.getText().toString();
                String inputPos = pos.getText().toString();

                // Checks
                if (inputName.length() < 3 || inputPos.length() < 3){
                    success = false;
                    failedAdd();
                }
                else{
                    success = true;
                }

                if (success){
                    IMyLocalBookingAPI api = IMyLocalBookingAPI.getApiInstance();
                    api.addEstablishment(new Establishment(
                            (Provider) MyLocalBooking.getCurrentUser(),
                            inputName, inputPos,
                            // TODO: replace with real coords and scaling ID
                            new Coordinates(45.4408f, 12.3154f), //near Venice coords
                            "0001"),
                            (a) -> confirmAdd(),
                            (b) -> {
                                        System.out.println("Data elaboration error");
                                        failedAdd();
                                    }
                    );
                    //confirmAdd();
                }

            }
        });


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        SelectableMapLocation location = (SelectableMapLocation) data.getExtras().getParcelable("selected_location");
                        // TODO: do something with this data
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

}