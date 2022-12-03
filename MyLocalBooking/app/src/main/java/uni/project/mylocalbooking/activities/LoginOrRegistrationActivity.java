package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uni.project.mylocalbooking.R;

public class LoginOrRegistrationActivity extends AppCompatActivity {

    private Button go_to_lgnButton;
    private Button go_to_intermediateRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_registration);

        go_to_lgnButton = findViewById(R.id.go_login_btn);
        go_to_intermediateRegistration = findViewById(R.id.go_registration_btn);

        go_to_lgnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });

        go_to_intermediateRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openIntermediateRegistrationActivity();
            }
        });

    }

    protected void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void openIntermediateRegistrationActivity() {
        Intent intent = new Intent(this, IntermediateRegistrationActivity.class);
        startActivity(intent);
    }

}