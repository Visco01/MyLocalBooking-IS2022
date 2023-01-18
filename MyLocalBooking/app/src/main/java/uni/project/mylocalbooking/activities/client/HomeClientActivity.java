package uni.project.mylocalbooking.activities.client;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.LoginActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.models.Establishment;

public class HomeClientActivity extends BaseNavigationActivity implements Adapter_search_establishment.IEstablishmentSelectedListener {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Adapter_search_establishment adapter;
    Switch switchPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionPreferences.getUserPrefs().isEmpty()){
            System.out.println("Empty sessionData");
            startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
        }

        if (isConnected()){
            System.out.println("Connected");
        }
        else{
            System.out.println("Not");
        }

        // Check if device has GPS at hardware level.
        if (MyLocalBooking.getAppContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_LOCATION_GPS)) {
        }
        // If device doesn't have GPS
        else {
            System.out.println("Device doesn't have GPS");
            switchPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (switchPos.isChecked()){
                        switchPos.setChecked(false);
                    }
                    deviceGPSfailure();
                }
            });
        }
    }

    // Register the permissions callback, which handles the user's response to the system permissions dialog.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted.
                } else {
                    // Permission not granted.
                    switchPos.setChecked(false);
                    deniedPermission();
                }
            });

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            System.out.println("Connectivity Exception: \n" + e.getMessage());
        }
        return connected;
    }

    private void initRecyclerView() {
        SwipeRefreshLayout refreshLayout = findViewById(R.id.client_establishments_refresh);
        refreshLayout.setOnRefreshListener(() -> {
            IMyLocalBookingAPI.getApiInstance().getClosestEstablishments(ests -> {
                establishments = ests.stream().collect(Collectors.toCollection(HashSet::new));
                initRecyclerView();
                refreshLayout.setRefreshing(false);
            }, null);
        });

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
    protected void onEstablishmentsReady(Collection<Establishment> establishments) {
        initRecyclerView();
    }

    @Override
    public void onEstablishmentSelected(Establishment establishment) {
        startActivity(
                new Intent(this, SlotListActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("current_establishment", establishment)
        );
    }

    private void deniedPermission(){
        DialogFragment newFragment = new FailureFragment("Permission Denied",
                "You need to give access to your location to use this feature!\n" +
                        "We need to get your position in order to find the establishments near by you..");
        newFragment.show(getSupportFragmentManager(), "failure");
    }

    private void deviceGPSfailure(){
        DialogFragment newFragment = new FailureFragment("GPS Not Found",
                "Your device does not have a working GPS system.\n" +
                        "This feature is available only to those who have one.\n" +
                        "Please check in your device setting if it's working correctly and contact us " +
                        "if it's just a problem of our App!");
        newFragment.show(getSupportFragmentManager(), "failure");
    }
}