package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class LandReviewActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getContentViewId(){
        return R.layout.activity_land_review;
    }

    public int getNavigationMenuItemId(){
        return R.id.reservations;
    }
}