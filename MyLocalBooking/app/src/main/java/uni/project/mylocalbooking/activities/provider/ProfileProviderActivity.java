package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class ProfileProviderActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getContentViewId(){
        return R.layout.activity_profile_provider;
    }

    public int getNavigationMenuItemId(){
        return R.id.profileProvider;
    }
}