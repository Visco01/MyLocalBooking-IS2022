package uni.project.mylocalbooking.activities.client;

import android.os.Bundle;
import android.widget.TextView;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class HomeClientActivity extends BaseNavigationActivity {

    private TextView username; // Testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = findViewById(R.id.userFullName);
        username.setText("Ciccio pasticcio");
    }

    // Returns the layout id, that is used by the super-class to manage the inflation
    public int getContentViewId(){
        return R.layout.activity_home_client;
    }

    // Returns the id in the navigation menu
    public int getNavigationMenuItemId(){
        return R.id.homeClient;
    }
}