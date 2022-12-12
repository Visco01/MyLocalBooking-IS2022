package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.client.ProfileClientActivity;

public class ChangeNStrikesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nstrikes);

        EditText newNStrikes = findViewById(R.id.newNStrikes);
        newNStrikes.setHint("2");

        // TODO: non va
        Button confirm = findViewById(R.id.confirm_change_nStrikes);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputValue = newNStrikes.getText().toString();
                if ((Integer.parseInt(inputValue)) < 1){
                    // TODO: replace with an error popup
                    System.out.println("Invalid Input < 1");
                    startActivity(new Intent(MyLocalBooking.getAppContext(), ProfileClientActivity.class));
                }
                else {
                    System.out.println("Good input" + inputValue);
                    // Update backend
                }
            }
        });
    }
}