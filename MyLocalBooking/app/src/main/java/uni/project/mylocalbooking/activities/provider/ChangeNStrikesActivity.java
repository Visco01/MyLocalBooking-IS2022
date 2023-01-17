package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;
import uni.project.mylocalbooking.models.Provider;

public class ChangeNStrikesActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nstrikes);
        Provider user = (Provider) MyLocalBooking.getCurrentUser();
        EditText newNStrikes = findViewById(R.id.newNStrikes);

        newNStrikes.setHint(Integer.toString(user.maxStrikes)); // Replace with current User
        Button confirm = findViewById(R.id.confirm_change_nStrikes);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputValue = newNStrikes.getText().toString();
                if (inputValue.isEmpty()){
                    failedEmptiness();
                }
                else if ((Integer.parseInt(inputValue)) < 1){
                    failedValid();
                }
                else {
                    user.maxStrikes = Integer.parseInt(inputValue);
                    // TODO: Fix stesso errore della ChangePassword con il parsing
                    // I/APICall error: com.android.volley.ParseError: org.json.JSONException:
                    // Value �� of type java.lang.String cannot be converted to JSONObject
                    // requestBody = "{"sample": "sample"}"
                    IMyLocalBookingAPI.getApiInstance().setMaxStrikes(
                            user.maxStrikes,
                            (a) -> success(),
                            (b) -> {
                                System.out.println("Error Set N Strikes API");
                                failedValid();
                            }
                    );
                }
            }
        });
    }

    public void success() {
        DialogFragment newFragment = new SuccessFragment("N° Strikes changed successfully!",
                "Your number of strikes needed before an automatic blacklist occurs was updated");
        newFragment.show(getSupportFragmentManager(), "success");
    }

    public void failedValid(){
        DialogFragment newFragment = new FailureFragment("Error, invalid input!",
                "The provided input is not valid, please choose a number greater than 1");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    public void failedEmptiness(){
        DialogFragment newFragment = new FailureFragment("Error, invalid input!",
                "The provided input is empty or its not recognized, please choose a number greater than 1");
        newFragment.show(getSupportFragmentManager(), "failure");
    }
}