package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class HomeProvider extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getContentViewId(){ return R.layout.activity_home_provider;}

    protected int getNavigationMenuItemId(){ return R.id.homeProvider;}
}