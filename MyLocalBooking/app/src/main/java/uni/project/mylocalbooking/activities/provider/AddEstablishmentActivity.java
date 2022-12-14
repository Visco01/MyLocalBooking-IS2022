package uni.project.mylocalbooking.activities.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class AddEstablishmentActivity extends AppCompatActivity {

    private FloatingActionButton backButton;
    private Button confirmButton;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_establishment);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), HomeProviderActivity.class));
            }
        });

        confirmButton = findViewById(R.id.confirm_add_est);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (success == true){
                    // TODO: update backend
                    confirmAdd();
                }
                else{
                    failedAdd();
                }
            }
        });

    }

    public void confirmAdd() {
        DialogFragment newFragment = new SuccessFragment("Added establishment successfully",
                "Your establishment was successfully added to the database, you can check it in 'My Establishment'");
        newFragment.show(getSupportFragmentManager(), "successAddEstablishment");
    }

    public void failedAdd(){
        DialogFragment newFragment = new FailureFragment("Added establishment failed",
                "At least one of the fields is not valid, please try again with a different input");
        newFragment.show(getSupportFragmentManager(), "failedAddEstablishment");
    }

}