package uni.project.mylocalbooking.activities.provider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Collection;
import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.activities.LoginActivity;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.Provider;

public class MyEstablishments extends BaseNavigationActivity implements Adapter_myEstablishment.EstablishmentSelected {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Adapter_myEstablishment adapter_myEstablishment;
    Provider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = (Provider) MyLocalBooking.getCurrentUser();

        if (SessionPreferences.getUserPrefs().isEmpty()){
            System.out.println("Empty sessionData");
            startActivity(new Intent(MyLocalBooking.getAppContext(), LoginActivity.class));
        }

        Button addButton = findViewById(R.id.addEstablishmentButton);
        addButton.setOnClickListener(view -> {
            startActivity(new Intent(MyLocalBooking.getAppContext(), AddEstablishmentActivity.class));
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

    private void initRecyclerView() {
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

    @Override
    protected void onEstablishmentsReady(Collection<Establishment> establishments) {
        initRecyclerView();
    }
}