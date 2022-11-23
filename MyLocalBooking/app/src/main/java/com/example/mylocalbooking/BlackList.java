package com.example.mylocalbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class BlackList extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<Model> blackList;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        initData();
        initRecycleReview();

    }

    private void initData() {
        blackList = new ArrayList<>();

        blackList.add(new Model(R.drawable.ic_baseline_person_24, "Antonio Labranca", "sonoUnaMerda@gmail.com"));
        blackList.add(new Model(R.drawable.ic_baseline_person_24, "Pietro Visconti", "visco00@gmail.com"));
        blackList.add(new Model(R.drawable.ic_baseline_person_24, "Pietro Donega", "pdonega2000@gmail.com"));
        blackList.add(new Model(R.drawable.ic_baseline_person_24, "Nicola Marizza", "marizzaNicola@gmail.com"));
        blackList.add(new Model(R.drawable.ic_baseline_person_24, "Mirco Mellara", "mircoMellara@gmail.com"));
    }

    private void initRecycleReview() {
        recyclerView = findViewById(R.id.recycleReview_blackList);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(blackList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}