package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Slot;

public class ReservationDetailActivity extends AppCompatActivity {

    FloatingActionButton backButton, deleteButton, reviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        backButton = findViewById(R.id.backButtonReservation);
        reviewButton = findViewById(R.id.reviewReservationButton);
        deleteButton = findViewById(R.id.deleteReservationButton);

        // TODO: set the reservation slot info and finish the constructor below


        // TODO: If the reservation date is today or in the future, you cannot review the establishment.
        /* PSEUDO-CODE
        if (reservationDate > todayDate){
            reviewButton.clickable = false;
            }
        */

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), MyBookings.class));
            }
        });

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), LandReviewActivity.class));
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                //IMyLocalBookingAPI.getApiInstance().cancelReservation(

                //);
            }
        });

    }
    /*
    public ReservationDetailActivity(String establishmentName, String establishmentAddress, Slot slot){

    } */
}