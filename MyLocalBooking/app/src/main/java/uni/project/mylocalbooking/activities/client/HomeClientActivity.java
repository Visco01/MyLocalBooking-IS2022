package uni.project.mylocalbooking.activities.client;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.UserTest;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;

public class HomeClientActivity extends BaseNavigationActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    public HashMap<Long, Establishment> establishments = new HashMap<>();
    Adapter_search_establishment adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserTest.setType("Client");
        super.onCreate(savedInstanceState);

        MutableLiveData<Collection<Establishment>> closestEstablishments = new MutableLiveData<>();
        closestEstablishments.observe(this, est -> {
            for(Establishment e : est)
                establishments.put(e.getId(), e);

            SlotListViewModel viewModel = new ViewModelProvider(this).get(SlotListViewModel.class);
            MyLocalBooking.establishments = establishments;

            initRecycleRview();
        });
        IMyLocalBookingAPI.getApiInstance().getClosestEstablishments(closestEstablishments);
    }

    private void initRecycleRview() {
        recyclerView = findViewById(R.id.recycleRview_establishment);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter_search_establishment(new ArrayList<>(establishments.values()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public int getContentViewId(){
        return R.layout.activity_home_client;
    }

    // Returns the id in the navigation menu
    public int getNavigationMenuItemId(){
        return R.id.homeClient;
    }

}