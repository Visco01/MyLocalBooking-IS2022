package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Map;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.client.LandReviewActivity;
import uni.project.mylocalbooking.activities.client.HomeClientActivity;

import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;

public class HomeActivity extends AppCompatActivity {

    private Button homePro;
    private Button homeCli;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homePro = findViewById(R.id.btn_comirs_home_provider);
        homeCli = findViewById(R.id.go_client_home);

        //Map<String, ?> sessionData = SessionPreferences.getUserPrefs();

        /*if (!sessionData.isEmpty()){
            System.out.println(sessionData);
            //TODO Comir.
        }*/

        homeCli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
            }
        });

        homePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openHomeProvider(); }
        });

        /*
        prenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), SlotListActivity.class));
            }
        });*/
    }


    protected void openHome() {
        Intent intent = new Intent(this, HomeClientActivity.class);
        startActivity(intent);
    }

    protected void openHomeProvider(){
        Intent intent = new Intent(this, HomeProviderActivity.class);
        startActivity(intent);
    }

}