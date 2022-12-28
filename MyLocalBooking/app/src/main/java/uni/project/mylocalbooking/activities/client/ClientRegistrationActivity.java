package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.UserTest;
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
                // TODO: missing position picker
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
                else if (!checkValidityCellphone(inputCellphone)){
                    failedValidCellphone();
                }
                else if (!checkPassword(inputPassword)){
                    failedValidPassword();
                }
                // TODO: underage check
                // Check if date is valid
                // Date picked < This year
                else if (Integer.parseInt(split[2]) >= 2022){
                    failedChangeMinAge();
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
                    api.register(new Client(defaultCoords, inputCellphone, inputEmail, inputName,
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
                "###PLACEHOLDER###");
        newFragment.show(getSupportFragmentManager(), "successReview");
    }

    private void confirmSignup() {
        UserTest.setType("Client");
        DialogFragment newFragment = new SuccessFragment("User created successfully!",
                "Enjoy ###PLACEHOLDER###");
        newFragment.show(getSupportFragmentManager(), "successReview");
    }

    private void confirmSignupUnderage() {
        DialogFragment newFragment = new SuccessFragment("User created successfully!",
                "Enjoy ###PLACEHOLDER###\n" +
                        "Remember you're underage, so you must use the application while a parent or" +
                        " someone overage is watching you!!!");
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
     * Cellphone must start with the char 3, because of Italian regulations
     * Moreover its length must be 10!
     * */
    private boolean checkValidityCellphone(String n){
        if (n.length() != 10){
            return false;
        }
        else if(n.charAt(0) != '3'){
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