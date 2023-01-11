package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.R;

public class PeopleWhoBook extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass_peopleWhoBook> peopleWhoBooks;
    Adapter_peopleWhoBook adapter_peopleWhoBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_who_book);

        initData();
        init_recycleRview();

    }

    private void init_recycleRview() {
        recyclerView = findViewById(R.id.peopleWhoBookRV);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter_peopleWhoBook = new Adapter_peopleWhoBook(peopleWhoBooks);
        recyclerView.setAdapter(adapter_peopleWhoBook);
        adapter_peopleWhoBook.notifyDataSetChanged();
    }

    private void initData() {
        peopleWhoBooks = new ArrayList<>();
        peopleWhoBooks.add(new ModelClass_peopleWhoBook("GianMarco morello", "gianc@gmail.com"));
        peopleWhoBooks.add(new ModelClass_peopleWhoBook("GianMarco morello", "gianc@gmail.com"));
        peopleWhoBooks.add(new ModelClass_peopleWhoBook("GianMarco morello", "gianc@gmail.com"));

    }
}