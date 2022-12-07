package uni.project.mylocalbooking.activities.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.UserTest;

public class HomeProviderActivity extends BaseNavigationActivity {

    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserTest.setType("Provider");
        super.onCreate(savedInstanceState);

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), AddEstablishmentActivity.class));
            }
        });
    }

    public int getContentViewId(){ return R.layout.activity_home_provider;}

    protected int getNavigationMenuItemId(){ return R.id.homeProvider;}
}