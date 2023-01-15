package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.fragments.AskConfirmFragment;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class ChangeNameActivity extends AppCompatActivity {

    private final String currentName = (String) SessionPreferences.getUserPrefs().get("firstname");
    private final String currentLastname = (String) SessionPreferences.getUserPrefs().get("lastname");
    private String inputValue1;
    private String inputValue2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        Button confirm = findViewById(R.id.confirm_change_name);
        EditText newName = findViewById(R.id.newName);
        EditText newSurname = findViewById(R.id.newSurname);

        // TODO: Update with currentUser name
        newName.setText(currentName);
        // TODO: Update with currentUser name
        newSurname.setText(currentLastname);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValue1 = newName.getText().toString();
                inputValue2 = newSurname.getText().toString();
                // Emptiness check
                if (inputValue1.isEmpty() || inputValue2.isEmpty()){
                    failedEmptiness();
                }
                // Other else ifs, relative to the constraints
                else if (!checkValidity(inputValue1, inputValue2)){
                    failedValid();
                }
                else {
                    // TODO: update backend
                    success();
                }
            }
        });
    }


    /**
     * Name/Lastname constraints.
     * Must be long at least 3 char
     * Is not in our list of banned names (like "Dog" or "Asd" or...).
     * */
    private boolean checkValidity(String name, String lastname){
        final String[] invalidNames = {"Cane", "Asd", "Dog"};
        if (name.length() < 3 || lastname.length() < 3){
            return false;
        }
        else if (Arrays.asList(invalidNames).contains(name) || Arrays.asList(invalidNames).contains(lastname)){
            return false;
        }
        else if (name.indexOf('\"') != -1 || name.contains("'")){
            return false;
        }
        else if (lastname.indexOf('\"') != -1 || lastname.contains("'")){
            return false;
        }
        return true;
    }

    private void success() {
        DialogFragment newFragment = new AskConfirmFragment("Are you sure?",
                "Are you sure you want to change your name from \"" + currentName + "\" to \"" + inputValue1 + "\"?\n" +
                "And your lastname from \"" + currentLastname + "\" to \"" + inputValue2 + "\"?\n",
                "Credentials updated successfully!",
                "Remember you risk an account suspension if they don't respect our policies!!");
        newFragment.show(getSupportFragmentManager(), "askConfirm");
 }

    private void failedEmptiness(){
        DialogFragment newFragment = new FailureFragment("Error, invalid credentials!",
                "At least one of the fields is empty, please fill them all");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    private void failedValid(){
        DialogFragment newFragment = new FailureFragment("Error, invalid credentials!",
                "Your given name/surname combination isn't valid.\n" +
                        "Please make sure both the name and lastname are long at least 3 characters " +
                        "and they do not contain any of the prohibited words or characters!\n");
        newFragment.show(getSupportFragmentManager(), "failure");
    }
}