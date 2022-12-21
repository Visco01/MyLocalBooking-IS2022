package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.AskConfirmFragment;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class ChangePasswordActivity extends AppCompatActivity {


    // TODO: get the value and remove the line below
    // For testing purpose i'll use the following one
    private final String currentPassword = "Ciao123";
    EditText oldPassword, newPassword, newPasswordConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        newPasswordConfirm = findViewById(R.id.newPasswordConfirm);

        Button confirmChange = findViewById(R.id.confirm_change_password);
        confirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputValue1 = oldPassword.getText().toString();
                String inputValue2 = newPassword.getText().toString();
                String inputValue3 = newPasswordConfirm.getText().toString();
                // Emptiness check
                if (inputValue1.isEmpty() || inputValue2.isEmpty() || inputValue3.isEmpty()){
                    failedChangeEmptiness();
                }
                // Check if old password matches
                else if(!currentPassword.equals(inputValue1)) {
                    failedChangeOld();
                }
                // Check if new password is valid (DB constraint
                else if(!checkPassword(inputValue2)){
                    failedChangeValid();
                }
                // Check if confirm is the same as newPassword
                else if(!inputValue2.equals(inputValue3)){
                    failedChangeEquals();
                }
                else{
                   confirmChange();
                }
            }
        });
    }

    /**
     * Password requisites:
     * Length: At least 6 characters.
     * At least one uppercase, one lowercase and one number.
     * */
    private boolean checkPassword(String p){
        int i;
        char c;
        boolean hasNumber = false, hasUppercase = false, hasLowercase = false;

        // If password is too short
        if (p.length() < 6){
            return false;
        }
        for (i = 0; i<p.length(); i++){
            c = p.charAt(i);
            if (Character.isDigit(c)){
                hasNumber = true;
            }
            else if (Character.isUpperCase(c)){
                hasUppercase = true;
            }
            else if (Character.isLowerCase(c)){
                hasLowercase = true;
            }
        }

        return hasNumber && hasUppercase && hasLowercase;
    }


    private void confirmChange() {
        DialogFragment newFragment = new AskConfirmFragment("Are you sure?",
                "Are you sure you want to change your password?",
                "Password changed successfully!",
                "Your password was changed successfully");
        newFragment.show(getSupportFragmentManager(), "askChange");
    }

    private void failedChangeEmptiness(){
        DialogFragment newFragment = new FailureFragment("Error",
                "You can't leave one or more fields empty!");
        newFragment.show(getSupportFragmentManager(), "failedChangeEmptiness");
    }

    private void failedChangeOld(){
        DialogFragment newFragment = new FailureFragment("Error",
                "Your old password doesn't match with the current one! Try again");
        newFragment.show(getSupportFragmentManager(), "failedChangeOld");
    }
    private void failedChangeValid(){
        DialogFragment newFragment = new FailureFragment("Error",
                "Your new password is not valid, please try again following these rules:\n" +
                        "Minimum length: 6 characters\n" +
                        "At least one Uppercase letter\n" +
                        "At least one Lowercase letter\n" +
                        "At least one Number");
        newFragment.show(getSupportFragmentManager(), "failedChangeValid");
    }

    private void failedChangeEquals(){
        DialogFragment newFragment = new FailureFragment("Error",
                "The new password was not confirmed correctly, please try again");
        newFragment.show(getSupportFragmentManager(), "failedChangeEquals");
    }

}