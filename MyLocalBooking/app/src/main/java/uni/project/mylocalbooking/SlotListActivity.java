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

        LocalDate currentDay = LocalDate.now();
        int current_dow = currentDay.getDayOfWeek().getValue();
        int monday_dow = DayOfWeek.MONDAY.getValue();

        if (current_dow > monday_dow)
            currentDay = currentDay.minusDays(current_dow - monday_dow);

        viewModel.setStartOfWeek(currentDay);
        viewModel.getCurrentDay().observe(this, dow -> {
            LocalDate startOfWeek = viewModel.getStartOfWeek().getValue();
            LocalDate selected = startOfWeek.plusDays(dow.getValue() - 1);

            System.out.println(selected);
        });
    }
}