package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.client.HomeClient;

public abstract class BaseNavigationActivity extends AppCompatActivity {

    protected BottomNavigationView navigationView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        System.out.println("View: " + getContentViewId());
        System.out.println("R.id.navView " + R.id.navView);
        System.out.println("findView: " + findViewById(R.id.navView));

        System.out.println("T1: " + R.layout.activity_base_navigation);
        System.out.println("T2: " + getParent());


        // --> null # DA FIXARE #
        navigationView = (BottomNavigationView) findViewById(R.id.navView);
        System.out.println(navigationView); // --> null # DA FIXARE #
        // navigationView.setOnNavigationItemSelectedListener(this); --> Deprecato

        if (navigationView != null) {
            navigationView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.homeClient:
                        intent = new Intent(getBaseContext(), HomeClient.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            });
            // Default position
            navigationView.setSelectedItemId(R.id.homeClient);
        }
        else{
            System.out.println("DEBUG: null navigationView");
        }

    }
/*

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



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationView.postDelayed(() -> {
            int itemId = item.getItemId();
            System.out.println(itemId);
            if (itemId == R.id.homeClient) {
                System.out.println("itemId correct");
                startActivity(new Intent(this, HomeClient.class));
            } else if (itemId == R.id.profile) {
                startActivity(new Intent(this, Profile.class));
            } else if (itemId == R.id.navigation_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));
            } finish();
}, 300);
        return true;
        }

private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
        }

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

*
* */
    public abstract int getContentViewId();

    protected abstract int getNavigationMenuItemId();

}