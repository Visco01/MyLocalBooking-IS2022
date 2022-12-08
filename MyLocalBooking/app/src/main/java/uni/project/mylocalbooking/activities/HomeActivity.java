package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.client.LandReviewActivity;
import uni.project.mylocalbooking.activities.client.MyBookings;
import uni.project.mylocalbooking.activities.client.Profile;
import uni.project.mylocalbooking.activities.client.RecycleRview_establishment;
import uni.project.mylocalbooking.activities.provider.BlackListActivity;
import uni.project.mylocalbooking.activities.provider.ProviderHomeActivity;

public class HomeActivity extends AppCompatActivity {

    private Button changePwdBtn;
    private Button reviewBtn;
    private Button changePhone;
    private Button changeDataOfBorn;
    private Button blackListBtn;
    private Button goMircosHome;
    private Button goHome;
    private Button goProfile;
    private Button goMyBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        blackListBtn = findViewById(R.id.btn_black_list);
        changePhone = findViewById(R.id.btn_change_phoneNumber);
        changePwdBtn = findViewById(R.id.change_pwd);
        reviewBtn = findViewById(R.id.make_review);
        changeDataOfBorn = findViewById(R.id.btn_change_DataOfBorn);
        goMircosHome = findViewById(R.id.go_mirco_home);
        goHome = findViewById(R.id.go_client_home);
        goProfile = findViewById(R.id.go_profile);
        goMyBookings = findViewById(R.id.go_myBookings);

        goMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMyBookings();
            }
        });

        goProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfile();
            }
        });

        goMircosHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProviderHomeActivity.class);
                startActivity(intent);
            }
        });

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
            }
        });

        blackListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBlackListActivity();
            }
        });

        changePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangePwd();
            }
        });

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReview();
            }
        });

        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangePhoneNumber();
            }
        });

        changeDataOfBorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeDataOfBorn();
            }
        });


    }

    protected void openProfile() {
        Intent intent = new Intent(HomeActivity.this, Profile.class);
        startActivity(intent);
    }

    protected void openHome() {
        Intent intent = new Intent(this, RecycleRview_establishment.class);
        startActivity(intent);
    }

    protected void openBlackListActivity() {
        Intent intent = new Intent(this, BlackListActivity.class);
        startActivity(intent);
    }

    protected void openChangePwd() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    protected void openReview() {
        Intent intent = new Intent(this, LandReviewActivity.class);
        startActivity(intent);
    }

    protected void openChangePhoneNumber() {
        Intent intent = new Intent(this, ChangePhoneNumberActivity.class);
        startActivity(intent);
    }

    protected void openChangeDataOfBorn() {
        Intent intent = new Intent(this, ChangeDataActivity.class);
        startActivity(intent);
    }

    protected void openMyBookings() {
        Intent intent = new Intent(this, MyBookings.class);
        startActivity(intent);
    }

}