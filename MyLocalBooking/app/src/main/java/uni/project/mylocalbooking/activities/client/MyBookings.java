package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.provider.AddEstablishmentActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Slot;

public class MyBookings extends BaseNavigationActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass_myBookings> myBookingsList;
    Adapter_myBookings adapter_myBookings;


    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IMyLocalBookingAPI api = IMyLocalBookingAPI.getApiInstance();

        List<Establishment> l = Arrays.stream(getIntent().getExtras().getBundle("establishments").getParcelableArray("establishments"))
                        .map(e -> (Establishment) e).collect(Collectors.toList());

        MutableLiveData<List<Slot>> res = new MutableLiveData<>();
        res.observe(this, reservations -> {
            System.out.println( );
        });

        try{
            api.getClientReservations(l,
                    (Long) SessionPreferences.getUserPrefs().get("subclass_id"), res);

            System.out.println(result);
        }catch (Throwable e){
            System.out.println("err");
        }

        System.out.println("1");

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