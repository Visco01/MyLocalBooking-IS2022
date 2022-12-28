package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Map;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.client.HomeClientActivity;

import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;
import uni.project.mylocalbooking.fragments.FailureFragment;

public class HomeActivity extends AppCompatActivity {

    private Button homePro;
    private Button homeCli;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homePro = findViewById(R.id.btn_comirs_home_provider);
        homeCli = findViewById(R.id.go_client_home);

        Map<String, ?> sessionData = SessionPreferences.getUserPrefs();

        if (!sessionData.isEmpty()){
            sessionData.forEach((k,v) -> System.out.println(k + ": " + v));
            //System.out.println(sessionData);
            //TODO Da rimuovere.
            if(((String) sessionData.get("usertype")).equals("client")){
                openHome();
            }
            else if (((String) sessionData.get("usertype")).equals("provider")){
                openHomeProvider();
            }
            else{
                System.out.println("Not a valid Login, entering \"UNAUTHORIZED MODE\"");
                failedLogin();
            }
        }

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

    private void failedLogin(){
        DialogFragment newFragment = new FailureFragment("Attention! Invalid Login",
                "Login failed; you are now using the app without local session data.\n" +
                        "Be careful because API calls implemented through the application will throw " +
                        "errors and won't work as intended.");
        newFragment.show(getSupportFragmentManager(), "failedAddEstablishment");
    }

}