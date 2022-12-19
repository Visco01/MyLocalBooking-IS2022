package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class ChangeNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        Button confirm = findViewById(R.id.confirm_change_name);
        EditText newName = findViewById(R.id.newName);
        EditText newSurname = findViewById(R.id.newSurname);
        // TODO: Update with currentUser name
        newName.setText("User's current name");
        // TODO: Update with currentUser name
        newSurname.setText("User's current surname");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputValue1 = newName.getText().toString();
                String inputValue2 = newSurname.getText().toString();
                if (inputValue1.isEmpty() || inputValue2.isEmpty()){
                    failedEmptiness();
                }
                // TODO: other else ifs, relative to the DB check constraints
                /*
                else if (...){
                    failedValid();
                }
                */

                else {
                    // TODO: update backend
                    success();
                }
            }
        });
    }

    private void success() {
        DialogFragment newFragment = new SuccessFragment("Credentials updated successfully!",
                "Remember you risk an account suspension if they don't respect our policies!!");
        newFragment.show(getSupportFragmentManager(), "success");
    }

    private void failedEmptiness(){
        DialogFragment newFragment = new FailureFragment("Error, invalid credentials!",
                "At least one of the fields is empty, please fill them all");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    private void failedValid(){
        DialogFragment newFragment = new FailureFragment("Error, invalid credentials!",
                "Your given name/surname combination isn't valid. Please try again with a different name or surname");
        newFragment.show(getSupportFragmentManager(), "failure");
    }
}