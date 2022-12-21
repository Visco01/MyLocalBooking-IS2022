package uni.project.mylocalbooking.activities.provider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.BaseNavigationActivity;

public class MyEstablishments extends BaseNavigationActivity implements RVInterface {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass_myEstablishment> myEstablishmentList;
    Adapter_myEstablishment adapter_myEstablishment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_data();
        init_recycleRview();

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
        adapter_myEstablishment = new Adapter_myEstablishment(myEstablishmentList, this);
        recyclerView.setAdapter(adapter_myEstablishment);
        adapter_myEstablishment.notifyDataSetChanged();
    }

    private void init_data() {

        myEstablishmentList = new ArrayList<>();
        myEstablishmentList.add(new ModelClass_myEstablishment(R.drawable.logo, "Campo Coletti", "Ruga giuffa 2345/1241"));
        myEstablishmentList.add(new ModelClass_myEstablishment(R.drawable.logo, "Campo Coletti", "Ruga giuffa 2345/1241"));
        myEstablishmentList.add(new ModelClass_myEstablishment(R.drawable.logo, "Campo Coletti", "Ruga giuffa 2345/1241"));


    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, CalendarViewActivity.class);
        startActivity(intent);
    }
}