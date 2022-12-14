package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.client.ProfileClientActivity;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class ChangeNStrikesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nstrikes);

        EditText newNStrikes = findViewById(R.id.newNStrikes);
        newNStrikes.setHint("2");
        Button confirm = findViewById(R.id.confirm_change_nStrikes);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputValue = newNStrikes.getText().toString();
                if (inputValue.isEmpty()){
                    failure2();
                }
                else if ((Integer.parseInt(inputValue)) < 1){
                    failure();
                }
                else {
                    // TODO: update backend
                    success();
                }
            }
        });
    }

    public void success() {
        DialogFragment newFragment = new SuccessFragment("N° Strikes changed successfully!",
                "Your number of strikes needed before an automatic blacklist occurs was updated");
        newFragment.show(getSupportFragmentManager(), "success");
    }

    public void failure(){
        DialogFragment newFragment = new FailureFragment("Error, invalid input!",
                "The provided input is not valid, please choose a number greater than 1");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    public void failure2(){
        DialogFragment newFragment = new FailureFragment("Error, invalid input!",
                "The provided input is not valid, please choose a NUMBER greater than 1");
        newFragment.show(getSupportFragmentManager(), "failure");
    }
}