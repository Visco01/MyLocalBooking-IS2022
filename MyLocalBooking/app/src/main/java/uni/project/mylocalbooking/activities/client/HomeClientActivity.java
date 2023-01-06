package uni.project.mylocalbooking.activities.client;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.HomeActivity;
import uni.project.mylocalbooking.activities.LoginActivity;
import uni.project.mylocalbooking.activities.UserTest;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.models.Establishment;

public class HomeClientActivity extends BaseNavigationActivity implements Adapter_search_establishment.IEstablishmentSelectedListener {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Collection<Establishment> establishments = new ArrayList<>();
    Adapter_search_establishment adapter;
    Switch switchPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserTest.setType("Client");
        super.onCreate(savedInstanceState);

        if (SessionPreferences.getUserPrefs().isEmpty()){
            System.out.println("Empty sessionData");
            startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
        }

        if(savedInstanceState == null) {
            MutableLiveData<Collection<Establishment>> closestEstablishments = new MutableLiveData<>();
            closestEstablishments.observe(this, est -> {
                establishments = est;
                initRecyclerView();
            });
            IMyLocalBookingAPI.getApiInstance().getClosestEstablishments(closestEstablishments);
        } else {
            for(Parcelable e : savedInstanceState.getParcelableArray("establishments"))
                establishments.add((Establishment) e);
        }


        switchPos = findViewById(R.id.switchPosition);

        // Check if device has GPS at hardware level.
        if (MyLocalBooking.getAppContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_LOCATION_GPS)) {

            switchPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // When switch is checked
                    if (switchPos.isChecked()){
                        System.out.println("Checked");

                        // If version is >= our build version
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            System.out.println("Build ok");

                            // If the permission was already given
                            if (ContextCompat.checkSelfPermission(MyLocalBooking.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED){
                                System.out.println("Already given");
                            }

                            /* Ask for the permission
                            else if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
                                // In an educational UI, explain to the user why your app requires this
                                // permission for a specific feature to behave as expected, and what
                                // features are disabled if it's declined. In this UI, include a
                                // "cancel" or "no thanks" button that lets the user continue
                                // using your app without granting the permission.
                                showInContextUI();
                            }*/

                            // If permission wasn't given before, ask harder
                            else{
                                System.out.println("Not given");
                                // Manage request code yourself
                                // requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 42); // Request code a piacere

                                // Managed by the system (better)
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                            }
                        }

                        // If version <= build version
                        else{
                            System.out.println("Build not ok");
                        }
                    }

                    // Unchecked the switch
                    else{
                        System.out.println("Unchecked");
                    }

                }
            });
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