package uni.project.mylocalbooking.activities.client;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.provider.EditChoiceDialogProviderActivity;
import uni.project.mylocalbooking.activities.provider.ProfileProviderActivity;

public class ProfileClientActivity extends BaseNavigationActivity {

    private ImageButton editButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editButton = findViewById(R.id.editButtonClient);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditChoiceDialogClientActivity ecd = new EditChoiceDialogClientActivity(ProfileClientActivity.this);
                ecd.show();
            }
        });
    }

    // Returns the layout id, that is used by the super-class to manage the inflation
    public int getContentViewId(){
        return R.layout.activity_profile_client;
    }

    // Returns the id in the navigation menu
    public int getNavigationMenuItemId(){
        return R.id.profileClient;
    }

}