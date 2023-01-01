package uni.project.mylocalbooking.activities.client;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.UserTest;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;

public class HomeClientActivity extends BaseNavigationActivity implements Adapter_search_establishment.IEstablishmentSelectedListener {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Collection<Establishment> establishments;
    Adapter_search_establishment adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserTest.setType("Client");
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            IMyLocalBookingAPI.getApiInstance().getClosestEstablishments(est -> {
                establishments = est;
            }, statusCode -> {
                System.out.println("GetClosestEstablishments returned error " + statusCode.name());
            });
        } else {
            for(Parcelable e : savedInstanceState.getParcelableArray("establishments"))
                establishments.add((Establishment) e);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Establishment[] establishmentsArr = new Establishment[establishments.size()];
        establishments.toArray(establishmentsArr);
        outState.putParcelableArray("establishments", establishmentsArr);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycleRview_establishment);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter_search_establishment(new ArrayList<>(establishments), this);
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

    @Override
    public void onEstablishmentSelected(Establishment establishment) {
        startActivity(
                new Intent(this, SlotListActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("current_establishment", establishment)
        );
    }
}