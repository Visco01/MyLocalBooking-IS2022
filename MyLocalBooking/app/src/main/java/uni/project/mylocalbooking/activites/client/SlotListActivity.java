package uni.project.mylocalbooking.activites.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.ListView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Provider;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListActivity extends AppCompatActivity {
    private SlotListViewModel viewModel;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_list);

        viewModel = new ViewModelProvider(this).get(SlotListViewModel.class);

        HashSet<DayOfWeek> weekdays = new HashSet<>();
        weekdays.add(DayOfWeek.FRIDAY);
        weekdays.add(DayOfWeek.SUNDAY);
        Provider provider = new Provider(true, null, 3, new HashMap<>(), "3475322555", "nicola.marizza@gmail.com", "Nicola", "Marizza", LocalDate.of(2000, 6, 10));
        Establishment establishment = new Establishment(provider, "Campetto da basket", "via ciao 13", new Coordinates(12, 12));

        List<SlotBlueprint> blueprints = new ArrayList<>();
        for (LocalTime start = LocalTime.of(8, 0); start.compareTo(LocalTime.of(22, 0)) < 0; start = start.plusMinutes(90)) {
            Integer reservationLimit = random.nextInt(11) > 6 ? null : 1 + random.nextInt(30);

            blueprints.add(new PeriodicSlotBlueprint(
                    start, start.plusMinutes(90), establishment, reservationLimit,
                    weekdays, LocalDate.of(2022, 11, 11), LocalDate.of(2022, 11, 15)
            ));


            for (SlotBlueprint elem : selectRandom(blueprints))
                for (int i = 0; i < elem.toDate.compareTo(elem.fromDate); i++) {
                    PeriodicSlot slot = new PeriodicSlot(LocalDate.now().plusDays(i), provider, (PeriodicSlotBlueprint) elem);

                    int amount = elem.reservationLimit != null ? elem.reservationLimit : 15;
                    if(random.nextInt(10) > 6)
                        amount = random.nextInt(amount + 1);
                    if(amount > 0 && random.nextInt(10) > 5)
                        slot.passwordProtected = true;

                    for(int j = 0; j < amount; j++)
                        slot.reservations.add(new Client(new Coordinates(1,1), "", "", "", "", LocalDate.now()));
                }

            viewModel.setBlueprints(blueprints);

            SlotListAdapter adapter = new SlotListAdapter(this, viewModel);

            viewModel.getCurrentDay().observe(this, adapter::refresh);

            ((ListView) getWindow().getDecorView().getRootView().findViewById(R.id.slot_list)).setAdapter(adapter);
        }
    }

    List<SlotBlueprint> selectRandom(List<SlotBlueprint> list) {
        Collections.shuffle(list);
        int start = random.nextInt(list.size());
        int len = start + random.nextInt(list.size() - start);
        return list.subList(start, len);
    }
}