package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class ChangeDataActivity extends AppCompatActivity {

    private EditText oldBornDate;
    private EditText newBornDate;
    private EditText confirmNewBornDate;
    private Calendar calendar;
    private Calendar calendar2;
    private Calendar calendar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_data);

        oldBornDate = findViewById(R.id.old_input_data);
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

                oldBornDate.setText(sdf.format(calendar.getTime()));
            }
        };

        oldBornDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ChangeDataActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        newBornDate = findViewById(R.id.new_born_data);
        calendar2 = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar2.set(Calendar.YEAR, year);
                calendar2.set(Calendar.MONTH, month);
                calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

                newBornDate.setText(sdf.format(calendar2.getTime()));
            }
        };

        newBornDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ChangeDataActivity.this, date2, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        confirmNewBornDate = findViewById(R.id.confirm_new_born_data);
        calendar3 = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date3 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar3.set(Calendar.YEAR, year);
                calendar3.set(Calendar.MONTH, month);
                calendar3.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

                confirmNewBornDate.setText(sdf.format(calendar3.getTime()));
            }
        };

        confirmNewBornDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ChangeDataActivity.this, date3, calendar3.get(Calendar.YEAR), calendar3.get(Calendar.MONTH), calendar3.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button confirmChange = findViewById(R.id.confirm_change_birthday);
        confirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Check emptiness

                // TODO: Check if date is valid

                // TODO: Check if underage

                // TODO: Check if confirm is the same

                // TODO: Confirm based on its age

            }
        });

    }

    private void confirmChange() {
        DialogFragment newFragment = new SuccessFragment("Birthday changed successfully!",
                "Your birthday was changed successfully, ");
        newFragment.show(getSupportFragmentManager(), "successReview");
    }

    private void confirmChangeUnderage() {
        DialogFragment newFragment = new SuccessFragment("Birthday changed successfully!",
                "Your birthday was changed successfully!\n" +
                        "Remember you're underage, so you must use the application while a parent or" +
                        " someone overage is watching you!!!");
        newFragment.show(getSupportFragmentManager(), "successReview");
    }

    private void failedEmptiness(){
        DialogFragment newFragment = new FailureFragment("Error",
                "At least one of the dates is empty, please fill them all");
        newFragment.show(getSupportFragmentManager(), "failedChangeMinAge");
    }

    private void failedChangeMinAge(){
        DialogFragment newFragment = new FailureFragment("Error",
                "The selected date is not valid, please try with another one");
        newFragment.show(getSupportFragmentManager(), "failedChangeMinAge");
    }

    private void failedChangeEquals(){
        DialogFragment newFragment = new FailureFragment("Error",
                "The new birthday was not confirmed correctly, please try again");
        newFragment.show(getSupportFragmentManager(), "failedChangeEquals");
    }

}