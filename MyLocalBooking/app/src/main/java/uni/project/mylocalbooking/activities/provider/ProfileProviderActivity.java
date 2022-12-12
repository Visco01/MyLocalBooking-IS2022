package uni.project.mylocalbooking.activities.provider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.ChangePasswordActivity;

public class ProfileProviderActivity extends BaseNavigationActivity {

    private ImageButton editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditChoiceDialogActivity ecd =new EditChoiceDialogActivity(ProfileProviderActivity.this);
                ecd.show();
            }
        });
    }

    public int getContentViewId(){
        return R.layout.activity_profile_provider;
    }

    public int getNavigationMenuItemId(){
        return R.id.profileProvider;
    }
}