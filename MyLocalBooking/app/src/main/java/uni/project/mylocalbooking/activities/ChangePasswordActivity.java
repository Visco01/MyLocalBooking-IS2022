package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import uni.project.mylocalbooking.R;

public class ChangePasswordActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getContentViewId(){ return R.layout.activity_change_password;}

    protected int getNavigationMenuItemId(){
        // if user.type == client
            // return R.id.profileClient
        // else
            return R.id.profileProvider;
    }

}