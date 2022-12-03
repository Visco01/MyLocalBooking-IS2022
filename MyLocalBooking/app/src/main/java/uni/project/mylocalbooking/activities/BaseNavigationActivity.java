package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.client.HomeClient;
import uni.project.mylocalbooking.activities.client.ProfileClient;

public abstract class BaseNavigationActivity extends AppCompatActivity {

    protected BottomNavigationView navigationView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // AppCompact super
        super.onCreate(savedInstanceState);

        // Set the layout of the corresponding Activity
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        // Default position
        navigationView.setSelectedItemId(R.id.homeClient);

        // Listener
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                // Home Client
                case R.id.homeClient:
                    intent = new Intent(getBaseContext(), HomeClient.class);
                    startActivity(intent);
                    return true;
                // Profile Client
                case R.id.profileClient:
                    intent = new Intent(getBaseContext(), ProfileClient.class);
                    startActivity(intent);
                    return true;
                /* Other cases*/
            }finish();
            return false;
        });
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
    /*
     ## These methods MUST BE implemented in EVERY SINGLE ONE of its subclasses, which
     means in EVERY Activity!! ##
    */
    // // Returns the layout id, that is used to manage the "inflation"
    public abstract int getContentViewId();

    // Returns the id in the navigation menu, used to set the correct item in the navbar
    protected abstract int getNavigationMenuItemId();

}