package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.AskConfirmFragment;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.Slot;

public class ReservationDetailActivity extends AppCompatActivity {

    FloatingActionButton deleteButton, reviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        reviewButton = findViewById(R.id.reviewReservationButton);
        deleteButton = findViewById(R.id.deleteReservationButton);

        Slot slot = (Slot) getIntent().getExtras().get("slot");
        Establishment establishment = (Establishment) getIntent().getExtras().get("establishment");

        TextView estName, estAddressDetail, resDate, hour, revDesc, delDesc;
        revDesc = findViewById(R.id.reviewDesc);
        delDesc = findViewById(R.id.deleteDesc);

        estName = findViewById(R.id.estName);
        estName.setText(establishment.name);
        estAddressDetail = findViewById(R.id.estAddressDetail);
        estAddressDetail.setText(establishment.address);
        resDate = findViewById(R.id.reservationDate);
        resDate.setText(slot.date.toString());
        hour = findViewById(R.id.reservationSlotHour);
        String hourStr;
        if (slot instanceof PeriodicSlot){
            hourStr = ((PeriodicSlot) slot).getStart().toString() + " - " + ((PeriodicSlot) slot).getEnd().toString();
        }
        else{
            hourStr = ((ManualSlot) slot).getStart().toString() + " - " + ((ManualSlot) slot).getEnd().toString();
        }
        hour.setText(hourStr);


        if (slot.date.isAfter(LocalDate.now())){
            reviewButton.setVisibility(View.GONE);
            revDesc.setVisibility(View.GONE);
        }
        else{
            deleteButton.setVisibility(View.GONE);
            delDesc.setVisibility(View.GONE);
        }

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyLocalBooking.getAppContext(), LandReviewActivity.class);
                intent.putExtra("establishment", establishment);
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMyLocalBookingAPI.getApiInstance().cancelReservation(slot,
                        (a) -> success(),
                        (b) -> failure()
                );
            }
        });

    }

    private void success() {
        DialogFragment newFragment = new AskConfirmFragment("Are you sure?",
                "Are you sure you want to delete this reservation?",
                "Reservation deleted",
                "Something went wrong, please contact the developers");
        newFragment.show(getSupportFragmentManager(), "askConfirmDelete");
    }

    private void failure(){
        DialogFragment newFragment = new FailureFragment("Server error",
                "Something went wrong, please contact the developers");
        newFragment.show(getSupportFragmentManager(), "failure");
    }
}