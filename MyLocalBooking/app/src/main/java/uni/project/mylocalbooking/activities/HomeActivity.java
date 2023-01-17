package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import java.util.Map;
import java.util.Objects;

import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.client.HomeClientActivity;

import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;
import uni.project.mylocalbooking.fragments.FailureFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Map<String, ?> sessionData = SessionPreferences.getUserPrefs();

        if (!sessionData.isEmpty()){
            sessionData.forEach((k,v) -> System.out.println(k + ": " + v));
            //System.out.println(sessionData);
            if(Objects.equals((String) sessionData.get("usertype"), "client")){
                openHome();
            }
            else if (Objects.equals((String) sessionData.get("usertype"), "provider")){
                openHomeProvider();
            }
            else{
                System.out.println("Not a valid Login, entering \"UNAUTHORIZED MODE\"");
                failedLogin();
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
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