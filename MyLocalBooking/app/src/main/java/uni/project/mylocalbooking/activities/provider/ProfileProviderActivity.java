package uni.project.mylocalbooking.activities.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.LoginActivity;
import uni.project.mylocalbooking.models.Provider;

public class ProfileProviderActivity extends BaseNavigationActivity {

    private ImageButton editButton, blacklistButton;
    private Button logoutButton;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private TextView password;
    private TextView email;
    private TextView numberPhone;
    private TextView numberOfStrikes;
    private TextView fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Provider provider = (Provider)MyLocalBooking.getCurrentUser();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        password = findViewById(R.id.passwordProvider);
        email = findViewById(R.id.emailProvider);
        numberPhone = findViewById(R.id.providerNumber);
        numberOfStrikes = findViewById(R.id.numberOfStrikesProvider);
        fullName = findViewById(R.id.fullNameProvider);
        password.setText(provider.password);
        email.setText(provider.email);
        numberPhone.setText(provider.cellphone);
        fullName.setText(provider.firstname + " " + provider.lastname);
        numberOfStrikes.setText(provider.maxStrikes);

        editButton = findViewById(R.id.editButtonProvider);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditChoiceDialogProviderActivity ecd = new EditChoiceDialogProviderActivity(ProfileProviderActivity.this);
                ecd.show();
            }
        });

        blacklistButton = findViewById(R.id.blacklistButton);
        blacklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), BlackListActivity.class));
            }
        });
        logoutButton = findViewById(R.id.logoutProviderButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                SessionPreferences.deleteSessionPreferences();
                // TODO: fix google login and adapt it
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

    public int getContentViewId(){
        return R.layout.activity_profile_provider;
    }

    public int getNavigationMenuItemId(){
        return R.id.profileProvider;
    }
}