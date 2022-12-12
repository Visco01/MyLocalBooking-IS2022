package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class ProfileProviderActivity extends BaseNavigationActivity {

    private ImageButton editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditChoiceDialogProviderActivity ecd =new EditChoiceDialogProviderActivity(ProfileProviderActivity.this);
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