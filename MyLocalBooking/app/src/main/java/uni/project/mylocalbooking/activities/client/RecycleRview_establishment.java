package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.R;

public class RecycleRview_establishment extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass_search_establishment> userList;
    Adapter_search_establishment adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_rview_establishment);

        initData();
        initRecycleRview();

    }

    private void initRecycleRview() {
        recyclerView = findViewById(R.id.recycleRview_establishment);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter_search_establishment(userList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        userList = new ArrayList<>();
        userList.add(new ModelClass_search_establishment(R.drawable.ic_baseline_event_note_24, "Campo calcetto Coletti", "drio casa mia 12/23"));
        userList.add(new ModelClass_search_establishment(R.drawable.ic_baseline_event_note_24, "Patronato culo cane", "vassaver"));
        userList.add(new ModelClass_search_establishment(R.drawable.ic_baseline_event_note_24, "Magazzino scoassa", "no savaria dirte 1727"));
        userList.add(new ModelClass_search_establishment(R.drawable.ic_baseline_event_note_24, "ciuccimeo", "drio casa mia 12/23"));
        userList.add(new ModelClass_search_establishment(R.drawable.ic_baseline_event_note_24, "Campo calcetto Coletti", "drio casa mia 12/23"));
        userList.add(new ModelClass_search_establishment(R.drawable.ic_baseline_event_note_24, "Campo calcetto Coletti", "drio casa mia 12/23"));
        userList.add(new ModelClass_search_establishment(R.drawable.ic_baseline_event_note_24, "Campo calcetto Coletti", "drio casa mia 12/23"));
        userList.add(new ModelClass_search_establishment(R.drawable.ic_baseline_event_note_24, "Campo calcetto Coletti", "drio casa mia 12/23"));


    }
}