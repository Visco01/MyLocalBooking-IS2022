package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        initRecyclerView();
    }

    private void initRecyclerView() {
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
        prenotationTodayList.add(new ModelPrenotationToday("Calcetto Coletti", "Mirco Mellara", "13:00:00", "14:00:00", "98273"));
        prenotationTodayList.add(new ModelPrenotationToday("Calcetto Coletti", "Pietro Donega", "17:00:00", "18:00:00", "07273"));

    }

    public int getContentViewId(){ return R.layout.activity_home_provider;}

    protected int getNavigationMenuItemId(){ return R.id.homeProvider;}

    @Override
    protected void onEstablishmentsReady(Collection<Establishment> establishments) {
        // TODO: catch
    }
}