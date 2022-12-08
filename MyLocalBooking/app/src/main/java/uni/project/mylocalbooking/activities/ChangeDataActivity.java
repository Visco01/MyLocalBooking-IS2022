package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import uni.project.mylocalbooking.R;

public class ChangeDataActivity extends BaseNavigationActivity {

    private EditText oldBornDate;
    private EditText newBornDate;
    private EditText confirmNewBornDate;
    private Calendar calendar;
    private Calendar calendar2;
    private Calendar calendar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    }

    public int getContentViewId(){ return R.layout.activity_change_data;}

    protected int getNavigationMenuItemId(){
        // if user.type == client
            // return R.id.profileClient
        // else
            return R.id.profileProvider;
    }
}