package uni.project.mylocalbooking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.client.MyBookings;
import uni.project.mylocalbooking.activities.client.ProfileClientActivity;
import uni.project.mylocalbooking.activities.client.HomeClientActivity;
import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;
import uni.project.mylocalbooking.activities.provider.MyEstablishments;
import uni.project.mylocalbooking.activities.provider.ProfileProviderActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;

public abstract class BaseNavigationActivity extends AppCompatActivity {

    protected BottomNavigationView navigationView;
    protected static HashSet<Establishment> establishments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // AppCompact super
        super.onCreate(savedInstanceState);

        // Set the layout of the corresponding Activity
        setContentView(getContentViewId());

        Map<String, ?> sessionData = SessionPreferences.getUserPrefs();
        if (sessionData.isEmpty()){
             startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
        }

        if(savedInstanceState != null && savedInstanceState.containsKey("establishments")) {
            establishments = Arrays.stream(savedInstanceState.getParcelableArray("establishments"))
                    .map(e -> (Establishment) e)
                    .collect(Collectors.toCollection(HashSet::new));
        }

        boolean isClient = Objects.equals((String) sessionData.get("usertype"), "client");
        if(isClient)
            startClient();
        else
            startProvider();
    }

    private void startProvider() {
        if(establishments == null) {
            IMyLocalBookingAPI.getApiInstance().getOwnedEstablishments(est -> {
                establishments = new HashSet<>(est);
                loadProviderNavigationOptions();
            }, code -> {
                System.out.println("ESTABLISHMENTS_ERROR" + code);
            });
        }
        else
            loadProviderNavigationOptions();
    }

    private void startClient() {
        if(establishments == null) {
            IMyLocalBookingAPI.getApiInstance().getClosestEstablishments(est -> {
                establishments = new HashSet<>(est);
                loadClientNavigationOptions();
            }, code -> {
                System.out.println("ESTABLISHMENTS_ERROR" + code);
            });
        }
        else
            loadClientNavigationOptions();
    }

    private void loadProviderNavigationOptions() {
        navigationView = (BottomNavigationView) findViewById(R.id.navigationProvider);

        // Default position
        navigationView.setSelectedItemId(R.id.homeProvider);

        // Listener
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeProvider:
                    startActivity(new Intent(getBaseContext(), HomeProviderActivity.class));
                    return true;
                case R.id.profileProvider:
                    startActivity(new Intent(getBaseContext(), ProfileProviderActivity.class));
                    return true;
                case R.id.establishments:
                    startActivity(new Intent(getBaseContext(), MyEstablishments.class));
                    return true;
            }finish();
            return false;
        });
        updateNavigationBarState();
        onEstablishmentsReady(establishments);
    }

    private void loadClientNavigationOptions() {
        navigationView = (BottomNavigationView) findViewById(R.id.navigationClient);
        // Default position
        navigationView.setSelectedItemId(R.id.homeClient);

        // Listener
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                // Home Client
                case R.id.homeClient:
                    startActivity(new Intent(getBaseContext(), HomeClientActivity.class));
                    return true;
                // Profile Client
                case R.id.profileClient:
                    startActivity(new Intent(getBaseContext(), ProfileClientActivity.class));
                    return true;
                // Reservations
                case R.id.reservations:
                    startActivity(new Intent(getBaseContext(), MyBookings.class));
                    return true;
            }finish();
            return false;
        });
        updateNavigationBarState();
        onEstablishmentsReady(establishments);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(establishments != null) {
            Establishment[] arr = new Establishment[establishments.size()];
            outState.putParcelableArray("establishments", establishments.toArray(arr));
        }
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    // Highlight the right element, based on the item, which depends on the Activity
    void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked) {
                item.setChecked(true);
                break;
            }
        }
    }

    /*                              !IMPORTANT!
            ## These methods MUST BE implemented in EVERY SINGLE ONE of its subclasses, which
            means in EVERY Activity!! ##
    */

    // Returns the layout id, that is used to manage the "inflation"
    // As an example, return R.layout.activity_`class_name`
    public abstract int getContentViewId();

    // Returns the id in the navigation menu, used to set the correct item in the navbar
    // If you are in the Change Credentials as a Client, you should put R.id.profileClient for example
    // If you are making a reservation, the R.id.homeClient
    // Else if you are writing a review, R.id.reservations
    protected abstract int getNavigationMenuItemId();

    protected abstract void onEstablishmentsReady(Collection<Establishment> establishments);
}