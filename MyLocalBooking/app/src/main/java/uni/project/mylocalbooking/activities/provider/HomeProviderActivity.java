package uni.project.mylocalbooking.activities.provider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;

public class HomeProviderActivity extends BaseNavigationActivity {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<ModelPrenotationToday> prenotationTodayList;
    AdapterPrenotationToday adapterPrenotationToday;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Date & Time");
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(android.icu.text.DateFormat.FULL).format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);

        init_data();
        init_recycleRview();

        IMyLocalBookingAPI.getApiInstance().getOwnedEstablishments(est -> {
            Intent intent = new Intent(this, Establishment_information.class).putExtra("establishment", est.stream().findFirst().get());
            startActivity(intent);
        }, code -> {
            System.out.println();
        });
    }

    private void init_recycleRview() {
        recyclerView = findViewById(R.id.prenotation_for_today_rv);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterPrenotationToday = new AdapterPrenotationToday(prenotationTodayList);
        recyclerView.setAdapter(adapterPrenotationToday);
        adapterPrenotationToday.notifyDataSetChanged();
    }

    private void init_data() {

        prenotationTodayList = new ArrayList<>();

        prenotationTodayList.add(new ModelPrenotationToday("Calcetto Coletti", "Giorgio rossi", "11:00:00", "12:00:00", "43123"));
        prenotationTodayList.add(new ModelPrenotationToday("Calcetto Coletti", "Giorgio rossi", "11:00:00", "12:00:00", "43123"));
        prenotationTodayList.add(new ModelPrenotationToday("Calcetto Coletti", "Giorgio rossi", "11:00:00", "12:00:00", "43123"));

    }

    public int getContentViewId(){ return R.layout.activity_home_provider;}

    protected int getNavigationMenuItemId(){ return R.id.homeProvider;}
}