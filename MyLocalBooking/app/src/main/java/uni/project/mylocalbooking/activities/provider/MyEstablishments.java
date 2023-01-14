package uni.project.mylocalbooking.activities.provider;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.LoginActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Provider;

public class MyEstablishments extends BaseNavigationActivity implements Adapter_myEstablishment.EstablishmentSelected {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Collection<Establishment> establishments = new ArrayList<>();
    Adapter_myEstablishment adapter_myEstablishment;
    private Button addButton;
    Provider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = (Provider) MyLocalBooking.getCurrentUser();
        /*
        init_data();

         */

        if (SessionPreferences.getUserPrefs().isEmpty()){
            System.out.println("Empty sessionData");
            startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
        }

        if (savedInstanceState == null) {
            IMyLocalBookingAPI.getApiInstance().getOwnedEstablishments(est -> {
                establishments = est;
                init_recycleRview();
            }, statusCode -> {
                System.out.println("getOwnedEstablishment return error" + statusCode.name());
            });
        }

        addButton = findViewById(R.id.addEstablishmentButton);
        // addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyLocalBooking.getAppContext(), AddEstablishmentActivity.class));
            }
        });

    }

    @Override
    public int getContentViewId() {
        return R.layout.my_establishments;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.establishments;
    }

    private void init_recycleRview() {

        recyclerView = findViewById(R.id.my_establishment_rv);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter_myEstablishment = new Adapter_myEstablishment(new ArrayList<>(establishments), this);
        recyclerView.setAdapter(adapter_myEstablishment);
        adapter_myEstablishment.notifyDataSetChanged();
    }

    @Override
    public void onEstablishmentSelected(Establishment establishment) {
        startActivity(new Intent(this, Past_provider_bookings.class).putExtra("current_establishment_selected", establishment));
    }

}