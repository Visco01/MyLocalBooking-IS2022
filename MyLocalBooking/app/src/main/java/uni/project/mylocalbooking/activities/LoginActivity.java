package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.models.AppUser;

public class LoginActivity extends AppCompatActivity {

    private Button goHome;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    ImageView googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!SessionPreferences.getUserPrefs().isEmpty()){
            System.out.println(SessionPreferences.getUserPrefs());
            if (MyLocalBooking.getCurrentUser() != null){
                System.out.println(MyLocalBooking.getCurrentUser());
                startActivity(new Intent(MyLocalBooking.getAppContext(),HomeActivity.class));
            }
        }
        else{
            System.out.println("Empty sessionData");
        }

        IMyLocalBookingAPI api = IMyLocalBookingAPI.getApiInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleBtn = findViewById(R.id.google_btn);

        goHome = findViewById(R.id.final_lgn_button);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Constraints
                EditText cell = findViewById(R.id.cellOrMailLogin);
                EditText psswd = findViewById(R.id.passwordLogin);
                if (cell.getText().toString().isEmpty() || psswd.getText().toString().isEmpty()){
                    failedEmptiness();
                }
                // Provider: 3332221113 - Psswd: Ciao123
                // Client: mirco@client.it / fwefwfs-->3332221114 - Psswd: Ciao123
                // mirco.client@gmail.com + 3332221115 - Psswd: Ciao123
                else{
                    // Errore 404 api/app_user_by_cellphone/<email> se loggo con la mail, ma funziona lo stesso
                    // Non critta la password per via di questo!
                    api.login(cell.getText().toString(), psswd.getText().toString(),
                            (a) -> openMenuActivity(), (b) -> System.out.println("Error with Login data")); //ToDO: fix
                }

            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        TextView redirect = findViewById(R.id.redirectSignupText);
        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), IntermediateRegistrationActivity.class));
            }
        });

    }

    protected void openMenuActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    protected void signIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateHome();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }

    protected void navigateHome() {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void failedEmptiness(){
        DialogFragment newFragment = new FailureFragment("Error, invalid credentials!",
                "At least one of the fields is empty, please fill them all");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    private void failedValid(){
        DialogFragment newFragment = new FailureFragment("Error, invalid credentials!",
                "Your given cellphone/email and password combination isn't valid.\n");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

}