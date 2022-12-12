package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class MyBookings extends BaseNavigationActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass_myBookings> myBookingsList;
    Adapter_myBookings adapter_myBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initRecyckeRview();
    }

    private void initRecyckeRview() {

        recyclerView = findViewById(R.id.my_bookings_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter_myBookings = new Adapter_myBookings(myBookingsList);
        recyclerView.setAdapter(adapter_myBookings);
        adapter_myBookings.notifyDataSetChanged();
    }

    private void initData() {

        myBookingsList = new ArrayList<>();
        myBookingsList.add(new ModelClass_myBookings(R.drawable.logo, "Campo coletti", "Baia del Re 5343/0909", "10.00"));
        myBookingsList.add(new ModelClass_myBookings(R.drawable.logo, "Campo coletti", "Baia del Re 5343/0909", "10.00"));
        myBookingsList.add(new ModelClass_myBookings(R.drawable.logo, "Campo coletti", "Baia del Re 5343/0909", "10.00"));
        myBookingsList.add(new ModelClass_myBookings(R.drawable.logo, "Campo coletti", "Baia del Re 5343/0909", "10.00"));

    }

    // Returns the layout id, that is used by the super-class to manage the inflation
    public int getContentViewId(){
        return R.layout.activity_my_bookings;
    }

    // Returns the id in the navigation menu
    public int getNavigationMenuItemId(){
        return R.id.reservations;
    }
}