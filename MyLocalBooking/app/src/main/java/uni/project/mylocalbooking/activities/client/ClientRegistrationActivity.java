package uni.project.mylocalbooking.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.MainActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;
import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;

public class ClientRegistrationActivity extends AppCompatActivity {

    private EditText bookerDateBorn;
    private Calendar today = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_registration);

        CheckBox cb = findViewById(R.id.client_signup_policy_cb);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(((CompoundButton) compoundButton).isChecked()){
                    System.out.println("Checked");
                } else {
                    System.out.println("Un-Checked");
                }
            }
        });

        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                view.cancelPendingInputEvents();
                // TODO: Update with privacy policy popup/activity
                startActivity(new Intent(MyLocalBooking.getAppContext(), MainActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };

        SpannableString ss = new SpannableString("here");
        ss.setSpan(cs, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence c = TextUtils.expandTemplate("I have read and I accept the Privacy and Data Processing Policy and Terms of use, of the application found ^1 ", ss);
        cb.setText(c);
        cb.setMovementMethod(LinkMovementMethod.getInstance());

        bookerDateBorn = findViewById(R.id.booker_registration_dateBorn);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

                bookerDateBorn.setText(sdf.format(calendar.getTime()));
            }

        };

        bookerDateBorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ClientRegistrationActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button confirmSignup = findViewById(R.id.confirm_client_registration);
        confirmSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Name and Surname
                EditText name = findViewById(R.id.client_signup_name);
                EditText lastname = findViewById(R.id.client_signup_surname);
                String inputName = name.getText().toString();
                String inputLastname = lastname.getText().toString();
                // Cellphone
                EditText cellphone = findViewById(R.id.client_signup_cellphone);
                String inputCellphone = cellphone.getText().toString();
                // Email (can be empty)
                EditText email = findViewById(R.id.client_signup_email);
                String inputEmail = email.getText().toString();
                // Password
                EditText password = findViewById(R.id.client_signup_password);
                String inputPassword = password.getText().toString();
                // Date
                EditText clientDate = findViewById(R.id.booker_registration_dateBorn);
                String dateInput = clientDate.getText().toString();
                String format = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                String todayString = sdf.format(today.getTime()).toString();
                String[] split = dateInput.split("/");
                // Check emptiness missing other fields
                if (dateInput.isEmpty() || inputName.isEmpty() || inputLastname.isEmpty() ||
                        inputCellphone.isEmpty()){
                    failedEmptiness();
                }
                // Check name/surname validity, relative to the constraints
                else if (!checkValidityNames(inputName, inputLastname)){
                    failedValidNameLastname();
                }
                // Cellphone validity
                else if (AppUser.tryFormatCellphone(inputCellphone) == null){
                    failedValidCellphone();
                }
                else if (!checkPassword(inputPassword)){
                    failedValidPassword();
                }
                // Check if date is valid
                // Date picked < This year
                else if (Integer.parseInt(split[2]) >= 2022){
                    failedChangeMinAge();
                }
                // Checked Policy
                else if (!cb.isChecked()){
                    failedChecked();
                }
                // Confirm based on its age
                else{
                    //confirmSignup();
                    IMyLocalBookingAPI api = IMyLocalBookingAPI.getApiInstance();
                    Coordinates defaultCoords = new Coordinates(45.4408f, 12.3155f); //Venice Default
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    formatter = formatter.withLocale(Locale.US);
                    LocalDate birthday = LocalDate.parse(dateInput, formatter);
                    System.out.println(birthday);
                    if (!SessionPreferences.getUserPrefs().isEmpty()){
                        SessionPreferences.deleteSessionPreferences();
                    }
                    api.register(new Client(defaultCoords, AppUser.tryFormatCellphone(inputCellphone), inputEmail, inputName,
                            inputLastname, birthday, inputPassword), inputPassword,
                            (a) -> confirmSignup(),
                            (b) -> failedApi()
                    );
                }
            }
        });

    }

    private void failedApi() {
        DialogFragment newFragment = new FailureFragment("API Error!",
                "Something went wrong...\nPlease try again later");
        newFragment.show(getSupportFragmentManager(), "successReview");
    }

    private void confirmSignup() {
        DialogFragment newFragment = new SuccessFragment("User created successfully!",
                "Your user has a default position already set in Venice!\n" +
                        "If you wish to change it with your home, feel free to do so in the your profile.\n\n" +
                        "Moreover, remember that if you're underage, you must use the application while a " +
                        "parent or someone overage is watching over you!!!");
        newFragment.show(getSupportFragmentManager(), "successReview");
    }

    private void failedEmptiness(){
        DialogFragment newFragment = new FailureFragment("Error",
                "At least one of the fields is empty, please fill them all");
        newFragment.show(getSupportFragmentManager(), "failedChangeMinAge");
    }

    private void failedChangeMinAge(){
        DialogFragment newFragment = new FailureFragment("Error",
                "The selected date is not valid, please try with another one");
        newFragment.show(getSupportFragmentManager(), "failedChangeMinAge");
    }

    private void failedValidNameLastname(){
        DialogFragment newFragment = new FailureFragment("Error, invalid credentials!",
                "Your given name/surname combination isn't valid.\n" +
                        "Please make sure both the name and lastname are long at least 3 characters " +
                        "and they do not contain any of the prohibited words or characters!, which are:\n" +
                        "  '   and   \"  ");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    private void failedValidCellphone(){
        DialogFragment newFragment = new FailureFragment("Error, invalid cellphone!",
                "Your given cellphone isn't valid.\n" +
                        "Please make sure it exists and its valid.");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    private void failedValidPassword() {
        DialogFragment newFragment = new FailureFragment("Error",
                "Your new password is not valid, please try again following these rules:\n" +
                        "Minimum length: 6 characters\n" +
                        "At least one Uppercase letter\n" +
                        "At least one Lowercase letter\n" +
                        "At least one Number");
        newFragment.show(getSupportFragmentManager(), "failedChangeValid");
    }

    private void failedChecked(){
        DialogFragment newFragment = new FailureFragment("Error, Policy not accepted",
                "You can't create a new account without accepting first our" +
                        "policies and terms of use!");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    /**
     * Checks if user is overage or not
     */
    private boolean isOverage(String date, String today){
        String[] D = date.split("/");
        String[] T = today.split("/");

        if (Integer.parseInt(D[2]) - Integer.parseInt(T[2]) >= 19){
            return true;
        }
        else if (Integer.parseInt(D[2]) - Integer.parseInt(T[2]) <= 17){
            return false;
        }
        else if (Integer.parseInt(D[0]) > Integer.parseInt(T[0])){
            return true;
        }
        else if (Integer.parseInt(D[0]) < Integer.parseInt(T[0])){
            return false;
        }
        else if (Integer.parseInt(D[1]) < Integer.parseInt(T[1])){
            return false;
        }

        return true;
    }

    /**
     * Name/Lastname constraints.
     * Must be long at least 3 char
     * Is not in our list of banned names (like "Dog" or "Asd" or...).
     * */
    private boolean checkValidityNames(String name, String lastname){
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

}