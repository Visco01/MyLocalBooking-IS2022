package uni.project.mylocalbooking.activities.client;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Slot;

public class MyBookings extends BaseNavigationActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    MutableLiveData<List<Slot>> res;
    Adapter_myBookings adapter_myBookings;
    List<Slot> slots = new ArrayList<>();
    HashMap<Slot, Establishment> resMap = new HashMap<>();
    List<Establishment> ests = new ArrayList<Establishment>();


    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IMyLocalBookingAPI api = IMyLocalBookingAPI.getApiInstance();

        MutableLiveData<List<Slot>> res = new MutableLiveData<>();
        res.observe(this, reservations -> {
            slots = res.getValue();
            for (Slot s: slots){
                ests.add(s.blueprint.establishment);
            }
            initRecyckeRview();
        });

        try{
            api.getClientReservations(establishments,
                    (Long) SessionPreferences.getUserPrefs().get("subclass_id"), res);

            System.out.println(result);
        }catch (Throwable e){
            System.out.println("err");
        }

    }

    private void initRecyckeRview() {

        recyclerView = findViewById(R.id.my_bookings_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter_myBookings = new Adapter_myBookings(slots, ests);
        recyclerView.setAdapter(adapter_myBookings);
        adapter_myBookings.notifyDataSetChanged();
    }

    /*
    private void initData() {

        myBookingsList = new ArrayList<>();
        myBookingsList.add(new ModelClass_myBookings(R.drawable.logo, "Campo coletti", "Baia del Re 5343/0909", "10.00"));
        myBookingsList.add(new ModelClass_myBookings(R.drawable.logo, "Campo coletti", "Baia del Re 5343/0909", "10.00"));
        myBookingsList.add(new ModelClass_myBookings(R.drawable.logo, "Campo coletti", "Baia del Re 5343/0909", "10.00"));
        myBookingsList.add(new ModelClass_myBookings(R.drawable.logo, "Campo coletti", "Baia del Re 5343/0909", "10.00"));

    }

    */

    // Returns the layout id, that is used by the super-class to manage the inflation
    public int getContentViewId(){
        return R.layout.activity_my_bookings;
    }

    // Returns the id in the navigation menu
    public int getNavigationMenuItemId(){
        return R.id.reservations;
    }

    @Override
    protected void onEstablishmentsReady(Collection<Establishment> establishments) {
        // TODO: catch
    }
}