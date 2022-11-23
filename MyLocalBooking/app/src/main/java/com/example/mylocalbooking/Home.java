package com.example.mylocalbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    private Button changePwdBtn;
    private Button reviewBtn;
    private Button changePhone;
    private Button changeDataOfBorn;
    private Button blackListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        blackListBtn = findViewById(R.id.btn_black_list);
        changePhone = findViewById(R.id.btn_change_phoneNumber);
        changePwdBtn = findViewById(R.id.change_pwd);
        reviewBtn = findViewById(R.id.make_review);
        changeDataOfBorn = findViewById(R.id.btn_change_DataOfBorn);

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

    protected void openBlackListActivity() {
        Intent intent = new Intent(this, BlackList.class);
        startActivity(intent);
    }

    protected void openChangePwd() {
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    protected void openReview() {
        Intent intent = new Intent(this, LandReview.class);
        startActivity(intent);
    }

    protected void openChangePhoneNumber() {
        Intent intent = new Intent(this, ChangePhoneNumber.class);
        startActivity(intent);
    }

    protected void openChangeDataOfBorn() {
        Intent intent = new Intent(this, ChangeData.class);
        startActivity(intent);
    }

}