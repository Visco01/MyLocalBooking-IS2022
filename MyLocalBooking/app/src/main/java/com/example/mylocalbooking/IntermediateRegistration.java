package com.example.mylocalbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntermediateRegistration extends AppCompatActivity {

    private Button bookerRegistration;
    private Button ownerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate_registration);

        bookerRegistration = findViewById(R.id.booker_registration);
        ownerRegistration = findViewById(R.id.owner_registration);

        bookerRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBookerRegistration();
            }
        });

        ownerRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOwnerRegistration();
            }
        });

    }

    protected void openBookerRegistration() {
        Intent intent = new Intent(this, BookerActivityRegistration.class);
        startActivity(intent);
    }

    protected void openOwnerRegistration() {
        Intent intent = new Intent(this, OwnerActivityRegistration.class);
        startActivity(intent);
    }

}