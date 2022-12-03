package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class HomeClient extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate HomeClient good");
        setContentView(R.layout.activity_home_client);
    }

    public int getContentViewId(){
        return R.layout.activity_home_client;
    }

    public int getNavigationMenuItemId(){
        return R.id.homeClient;
    }
}