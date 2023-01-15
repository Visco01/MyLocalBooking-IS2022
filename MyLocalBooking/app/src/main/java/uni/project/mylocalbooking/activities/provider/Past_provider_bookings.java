package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.LoginActivity;
import uni.project.mylocalbooking.models.Establishment;

public class Past_provider_bookings extends AppCompatActivity implements RVInterface{

    Establishment establishment;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass_pastProviderBookings> pastProviderBookings;
    Collection<Establishment> establishments = new ArrayList<>();
    AdapterPastProviderBookings adapterPastProviderBookings;
    LocalDate currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        establishment = (Establishment) getIntent().getExtras().getParcelable("current_establishment_selected");
        currentDate = LocalDate.now();
        currentDate.minusDays(1);
        setContentView(R.layout.activity_past_provider_bookings);
        initData();
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
        adapterPastProviderBookings = new AdapterPastProviderBookings(this, pastProviderBookings);
        recyclerView.setAdapter(adapterPastProviderBookings);
        adapterPastProviderBookings.notifyDataSetChanged();

    }

    private void initData() {
        pastProviderBookings = new ArrayList<>();
        pastProviderBookings.add(new ModelClass_pastProviderBookings("Porto san giorgio", "Topo Gigio", "11/01/2021"));
        pastProviderBookings.add(new ModelClass_pastProviderBookings("Porto san giorgio", "Topo Gigio", "11/01/2021"));
        pastProviderBookings.add(new ModelClass_pastProviderBookings("Porto san giorgio", "Topo Gigio", "11/01/2021"));
        pastProviderBookings.add(new ModelClass_pastProviderBookings("Porto san giorgio", "Topo Gigio", "11/01/2021"));
    }

    @Override
    public void onItemClick(int position) {

    }
}