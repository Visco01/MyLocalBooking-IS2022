package uni.project.mylocalbooking.activities.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class AddEstablishmentActivity extends BaseNavigationActivity {

    private FloatingActionButton backButton;
    private Button confirmButton;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(success);
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
                    confirmAdd();
                }
                else{
                    failedAdd();
                }
            }
        });

    }

    public void confirmAdd() {
        DialogFragment newFragment = new SuccessFragment();
        newFragment.show(getSupportFragmentManager(), "successAddEstablishment");
    }

    public void failedAdd(){
        DialogFragment newFragment = new FailureFragment();
        newFragment.show(getSupportFragmentManager(), "failedAddEstablishment");
    }

    public int getContentViewId(){ return R.layout.activity_add_establishment; }

    public int getNavigationMenuItemId(){ return R.id.homeProvider; }
}