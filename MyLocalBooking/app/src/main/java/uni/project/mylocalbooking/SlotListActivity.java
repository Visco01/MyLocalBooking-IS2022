package uni.project.mylocalbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class SlotListActivity extends AppCompatActivity {
    private SlotListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_list);

        viewModel = new ViewModelProvider(this).get(SlotListViewModel.class);
    }
}