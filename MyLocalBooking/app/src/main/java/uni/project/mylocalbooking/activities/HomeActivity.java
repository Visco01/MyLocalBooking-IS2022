package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uni.project.mylocalbooking.activities.client.HomeClientActivity;
import uni.project.mylocalbooking.activities.client.LandReviewActivity;
import uni.project.mylocalbooking.activities.provider.BlackListActivity;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;

public class HomeActivity extends AppCompatActivity {

    private Button changePwdBtn;
    private Button reviewBtn;
    private Button changePhone;
    private Button changeDataOfBorn;
    private Button blackListBtn;
    private Button comirsHome;
    private Button comirsHome2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        blackListBtn = findViewById(R.id.btn_black_list);
        changePhone = findViewById(R.id.btn_change_phoneNumber);
        changePwdBtn = findViewById(R.id.change_pwd);
        reviewBtn = findViewById(R.id.make_review);
        changeDataOfBorn = findViewById(R.id.btn_change_DataOfBorn);
        comirsHome = findViewById(R.id.btn_comirs_home_client);
        comirsHome2 = findViewById(R.id.btn_comirs_home_provider);

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

        comirsHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openHomeClient(); }
        });

        comirsHome2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openHomeProvider(); }
        });

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

    protected void openHomeClient(){
        Intent intent = new Intent(this, HomeClientActivity.class);
        startActivity(intent);
    }

    protected void openHomeProvider(){
        Intent intent = new Intent(this, HomeProviderActivity.class);
        startActivity(intent);
    }

}