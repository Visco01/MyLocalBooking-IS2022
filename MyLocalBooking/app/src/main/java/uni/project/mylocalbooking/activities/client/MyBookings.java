package uni.project.mylocalbooking.activities.client;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.SelectableMapLocation;
import uni.project.mylocalbooking.models.Slot;

public class MyBookings extends BaseNavigationActivity implements Adapter_myBookings.IReservationSelectedListener, ActivityResultCallback<ActivityResult> {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Adapter_myBookings adapter_myBookings;
    private List<Slot> slots = new ArrayList<>();
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this);

    private void initRecyclerView() {
        if(recyclerView == null) {
            recyclerView = findViewById(R.id.my_bookings_recyclerView);
            layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
        }
        recyclerView.setLayoutManager(layoutManager);
        adapter_myBookings = new Adapter_myBookings(this, slots);
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
        loadSlots();
    }

    private void loadSlots() {
        MutableLiveData<List<Slot>> res = new MutableLiveData<>();
        res.observe(this, reservations -> {
            this.slots = reservations;
            initRecyclerView();
        });

        try{
            IMyLocalBookingAPI.getApiInstance().getClientReservations(establishments,
                    (Long) SessionPreferences.getUserPrefs().get("subclass_id"), res);
        }catch (Throwable e){
            System.out.println("err");
        }
    }

    @Override
    public void onReservationSelected(Slot slot) {
        Intent intent = new Intent(this, ReservationDetailActivity.class);
        intent.putExtra("establishment", slot.blueprint.establishment);
        intent.putExtra("slot", slot);
        intent.putExtra("blueprint", slot.blueprint);
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            Establishment establishment = (Establishment) data.getExtras().getParcelable("establishment");
            establishments.add(establishment);
            loadSlots();
        }
    }
}