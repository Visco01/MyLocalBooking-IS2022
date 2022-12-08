package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.ChangePasswordActivity;

public class Profile extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Returns the layout id, that is used by the super-class to manage the inflation
    public int getContentViewId(){
        return R.layout.activity_profile;
    }

    // Returns the id in the navigation menu
    public int getNavigationMenuItemId(){
        return R.id.profileClient;
    }

}