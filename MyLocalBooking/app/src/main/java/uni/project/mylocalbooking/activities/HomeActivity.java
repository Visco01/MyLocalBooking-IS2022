package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.client.LandReviewActivity;
import uni.project.mylocalbooking.activities.client.RecycleRview_establishment;
import uni.project.mylocalbooking.activities.client.SlotListActivity;
import uni.project.mylocalbooking.activities.provider.BlackListActivity;

import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;

public class HomeActivity extends AppCompatActivity {

    private Button changePwdBtn;
    private Button reviewBtn;
    private Button changePhone;
    private Button changeDataOfBorn;
    private Button blackListBtn;
    private Button comirsHome2;
    private Button goHome;
    private Button prenota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        blackListBtn = findViewById(R.id.btn_black_list);
        changePhone = findViewById(R.id.btn_change_phoneNumber);
        changePwdBtn = findViewById(R.id.change_pwd);
        reviewBtn = findViewById(R.id.make_review);
        changeDataOfBorn = findViewById(R.id.btn_change_DataOfBorn);
        comirsHome2 = findViewById(R.id.btn_comirs_home_provider);
        goHome = findViewById(R.id.go_client_home);
        prenota = findViewById(R.id.prenota);


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

        comirsHome2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openHomeProvider(); }
        });

        prenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), SlotListActivity.class));
            }
        });
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

    protected void openHomeProvider(){
        Intent intent = new Intent(this, HomeProviderActivity.class);
        startActivity(intent);
    }


}