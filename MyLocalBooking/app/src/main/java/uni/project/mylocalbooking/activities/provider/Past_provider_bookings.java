package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.LoginActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class Past_provider_bookings extends AppCompatActivity implements RVInterface{

    Establishment establishment;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdapterPastProviderBookings adapterPastProviderBookings;
    LocalDate currentDate;
    ArrayList<Slot> allTodaySlots = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        establishment = (Establishment) getIntent().getExtras().getParcelable("current_establishment_selected");
        currentDate = LocalDate.now();
        currentDate.minusDays(1);
        setContentView(R.layout.activity_past_provider_bookings);

        try {
            Collection<SlotBlueprint> slotsBlueprint = establishment.getBlueprints(currentDate);
            for (SlotBlueprint s: slotsBlueprint){
                HashMap<LocalDate, List<Slot>> i = s.slots;
                List<Slot> ss = i.get(LocalDate.now().minusDays(1));
                if (ss == null){
                    //TODO capire che farci
                    System.out.println("No reservations");
                }
                allTodaySlots.addAll(ss);
            }

        } catch (Establishment.PartialReservationsResultsException e) {
            e.printStackTrace();
        }
        initRecycleRview();

        if (SessionPreferences.getUserPrefs().isEmpty()){
            System.out.println("Empty sessionData");
            startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
        }

    }

    private void initRecycleRview() {
        recyclerView = findViewById(R.id.rvPastProviderBookings);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapterPastProviderBookings = new AdapterPastProviderBookings(this, allTodaySlots);
        recyclerView.setAdapter(adapterPastProviderBookings);
        adapterPastProviderBookings.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(int position) {

    }
}