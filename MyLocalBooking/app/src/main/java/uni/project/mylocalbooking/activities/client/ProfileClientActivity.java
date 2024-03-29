package uni.project.mylocalbooking.activities.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.LoginActivity;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Establishment;

public class ProfileClientActivity extends BaseNavigationActivity {

    private ImageButton editButton;
    private Button logoutButton;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private TextView clientNumber;
    private TextView fullName;
    private TextView email;
    private TextView passwordClient;
    private TextView birthday;
    private TextView position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Client client = (Client)MyLocalBooking.getCurrentUser();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        editButton = findViewById(R.id.editButtonClient);
        position = findViewById(R.id.clientPosition);
        clientNumber = findViewById(R.id.clientNumber);
        fullName = findViewById(R.id.fullNameClient);
        email = findViewById(R.id.emailClient);
        passwordClient = findViewById(R.id.passwordClient);
        birthday = findViewById(R.id.birthDayClient);
        email.setText(client.email);
        position.setText(((Double)client.position.latitude).toString().substring(0,7) + " " + ((Double)client.position.longitude).toString().substring(0, 7));
        clientNumber.setText(client.cellphone);
        fullName.setText(client.firstname + " " + client.lastname);
        birthday.setText(client.dob.toString());

        passwordClient.setText(client.password);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditChoiceDialogClientActivity ecd = new EditChoiceDialogClientActivity(ProfileClientActivity.this);
                ecd.show();
            }
        });

        logoutButton = findViewById(R.id.logoutClientButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                establishments = null; // TODO: fix lazy workaround
                MyLocalBooking.clearSessionData();
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
                    }
                });
                startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
            }
        });
    }

    // Returns the layout id, that is used by the super-class to manage the inflation
    public int getContentViewId(){
        return R.layout.activity_profile_client;
    }

    // Returns the id in the navigation menu
    public int getNavigationMenuItemId(){
        return R.id.profileClient;
    }

    @Override
    protected void onEstablishmentsReady(Collection<Establishment> establishments) {
        // TODO: catch
    }
}