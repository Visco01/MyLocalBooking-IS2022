package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.R;

public class Past_provider_bookings extends AppCompatActivity implements RVInterface{

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass_pastProviderBookings> pastProviderBookings;
    AdapterPastProviderBookings adapterPastProviderBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_provider_bookings);
        initData();
        initRecycleRview();


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