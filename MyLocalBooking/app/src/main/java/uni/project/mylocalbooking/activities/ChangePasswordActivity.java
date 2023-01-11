package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Map;

import io.opencensus.internal.StringUtils;
import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.api.AESCrypt;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.AskConfirmFragment;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class ChangePasswordActivity extends AppCompatActivity {


    // TODO: get the value and remove the line below
    // For testing purpose i'll use the following one
    Map<String, ?> sessionData = SessionPreferences.getUserPrefs();
    private String currentPassword = (String) sessionData.get("password");
    EditText oldPassword, newPassword, newPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = currentPassword.replaceAll("\\s+","");

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
                else {
                    try {
                        System.out.println(currentPassword);
                        System.out.println(AESCrypt.encrypt(inputValue1));
                        String cr = AESCrypt.encrypt(inputValue1);
                        cr = cr.replaceAll("\\s+","");
                        if(!currentPassword.equals(cr)) {
                            System.out.println("CUR: " + currentPassword);
                            for (int i=0; i<currentPassword.length(); i++){
                                System.out.println(currentPassword.charAt(i));
                            }
                            System.out.println("-----\n");
                            System.out.println("CRY: " + cr);
                            for (int i=0; i<cr.length(); i++){
                                System.out.println(cr.charAt(i));
                            }
                            System.out.println("-----\n");
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
                            // Errore nel parsing di una qualche stringa nel JSON (non è colpa della mail null)
                            IMyLocalBookingAPI api = IMyLocalBookingAPI.getApiInstance();
                            api.changeUserPassword(inputValue2.replaceAll("\\s+",""),
                                    (a) -> confirmChange(),
                                    (b) -> {
                                            System.out.println("Possible data error, change password api failed");
                                            failedChangeAPI();
                                            }
                            );
                            //confirmChange();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private void failedChangeAPI(){
        DialogFragment newFragment = new FailureFragment("API error",
                "Given data is not valid or the service is not working correctly right now," +
                        "please try again later or contact us");
        newFragment.show(getSupportFragmentManager(), "failedChangeEquals");
    }

}