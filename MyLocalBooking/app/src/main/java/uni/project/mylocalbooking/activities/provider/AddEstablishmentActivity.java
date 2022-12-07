package uni.project.mylocalbooking.activities.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class AddEstablishmentActivity extends BaseNavigationActivity {

    private FloatingActionButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), HomeProviderActivity.class));
            }
        });
    }

    public int getContentViewId(){ return R.layout.activity_add_establishment; }

    public int getNavigationMenuItemId(){ return R.id.homeProvider; }
}