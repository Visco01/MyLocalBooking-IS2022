package uni.project.mylocalbooking.activities.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.LoginActivity;

public class ProfileProviderActivity extends BaseNavigationActivity {

    private ImageButton editButton, blacklistButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editButton = findViewById(R.id.editButtonProvider);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditChoiceDialogProviderActivity ecd = new EditChoiceDialogProviderActivity(ProfileProviderActivity.this);
                ecd.show();
            }
        });

        blacklistButton = findViewById(R.id.blacklistButton);
        blacklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), BlackListActivity.class));
            }
        });
        logoutButton = findViewById(R.id.logoutProviderButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
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