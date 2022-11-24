package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uni.project.mylocalbooking.R;

public class LoginActivity extends AppCompatActivity {

    private Button goHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        goHome = findViewById(R.id.final_lgn_button);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuActivity();
            }
        });

    }

    protected void openMenuActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}