package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.client.MyBookings;
import uni.project.mylocalbooking.activities.client.ProfileClientActivity;
import uni.project.mylocalbooking.activities.client.HomeClientActivity;
import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;
import uni.project.mylocalbooking.activities.provider.MyEstablishments;
import uni.project.mylocalbooking.activities.provider.ProfileProviderActivity;

public abstract class BaseNavigationActivity extends AppCompatActivity {

    protected BottomNavigationView navigationView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // AppCompact super
        super.onCreate(savedInstanceState);

        // Set the layout of the corresponding Activity
        setContentView(getContentViewId());

        // TODO: Remove before submission
        // Get the currentUser info
        //Map<String, ?> sessionData = SessionPreferences.getUserPrefs();
        // if (!sessionData){
        //      return to login page!!!
        // }
        //if (((String) sessionData.get("usertype")).equals("client")){

        if (UserTest.getType() == "Client"){
            navigationView = (BottomNavigationView) findViewById(R.id.navigationClient);
            // Default position
            navigationView.setSelectedItemId(R.id.homeClient);

            // Listener
            navigationView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    // Home Client
                    case R.id.homeClient:
                        intent = new Intent(getBaseContext(), HomeClientActivity.class);
                        startActivity(intent);
                        return true;
                    // Profile Client
                    case R.id.profileClient:
                        intent = new Intent(getBaseContext(), ProfileClientActivity.class);
                        startActivity(intent);
                        return true;
                    // Reservations
                    case R.id.reservations:
                        intent = new Intent(getBaseContext(), MyBookings.class);
                        startActivity(intent);
                        return true;
                }finish();
                return false;
            });
        }

        else if (UserTest.getType() == "Provider"){

            navigationView = (BottomNavigationView) findViewById(R.id.navigationProvider);

            // Default position
            navigationView.setSelectedItemId(R.id.homeProvider);

            // Listener
            navigationView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.homeProvider:
                        intent = new Intent(getBaseContext(), HomeProviderActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.profileProvider:
                        intent = new Intent(getBaseContext(), ProfileProviderActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.establishments:
                        intent = new Intent(getBaseContext(), MyEstablishments.class);
                        startActivity(intent);
                        return true;
                }finish();
                return false;
            });
        }

        else{
            System.out.println("User Type Error");
        }


    }

    // Highlights the current item on the NavBar the first time the user launches the app
    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
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

}