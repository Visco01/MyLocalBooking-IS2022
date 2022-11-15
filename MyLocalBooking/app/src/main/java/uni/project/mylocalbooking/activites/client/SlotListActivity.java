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
import uni.project.mylocalbooking.fragments.ProvideSlotPasswordDialogFragment;
import uni.project.mylocalbooking.fragments.SetSlotPasswordDialogFragment;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Provider;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListActivity extends AppCompatActivity implements SlotListAdapter.IListener {
    private SlotListViewModel viewModel;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_list);

        viewModel = new ViewModelProvider(this).get(SlotListViewModel.class);

        viewModel.setBlueprints(generateSampleData());

        SlotListAdapter adapter = new SlotListAdapter(this);

        viewModel.getCurrentDay().observe(this, dow -> {
            LocalDate weekStart = viewModel.getStartOfWeek().getValue();
            LocalDate selected = weekStart.plusDays(dow.getValue() - 1);
            List<SlotBlueprint> slotItems = viewModel.getBlueprints(selected);

            adapter.onRefresh(selected, slotItems);
        });

        ((ListView) getWindow().getDecorView().getRootView().findViewById(R.id.slot_list)).setAdapter(adapter);
    }

    @Override
    public void onSlotReservationToggled(ISelectableSlot selectableSlot) {
        if(selectableSlot instanceof SlotBlueprint) {
            SetSlotPasswordDialogFragment dialog = new SetSlotPasswordDialogFragment(new SetSlotPasswordDialogFragment.IListener() {
                @Override
                public void onAccepted(ISelectableSlot slot) {
                    showPasswordInputDialog(slot, R.string.choose_slot_password);
                }

                @Override
                public void onRefused(ISelectableSlot slot) {
                    makeReservation(slot, null);
                }
            }, selectableSlot);
            dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
        } else if(selectableSlot.isPasswordProtected()) {
            showPasswordInputDialog(selectableSlot, R.string.slot_password_required);
        }
        else {
            makeReservation(selectableSlot, null);
        }
    }

    private void makeReservation(ISelectableSlot slot, String password) {
        viewModel.makeReservation(slot, password);
    }

    private void showPasswordInputDialog(ISelectableSlot slot, int titleId) {
        ProvideSlotPasswordDialogFragment dialog = new ProvideSlotPasswordDialogFragment(new ProvideSlotPasswordDialogFragment.IListener() {
            @Override
            public void onSlotPasswordSubmitted(ISelectableSlot slot, String password) {
                makeReservation(slot, password);
            }
        }, slot, titleId);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private List<SlotBlueprint> generateSampleData() {
        HashSet<DayOfWeek> weekdays = new HashSet<>();
        weekdays.add(DayOfWeek.FRIDAY);
        weekdays.add(DayOfWeek.SUNDAY);
        Provider provider = new Provider(true, null, 3, new HashMap<>(), "3475322555", "nicola.marizza@gmail.com", "Nicola", "Marizza", LocalDate.of(2000, 6, 10));
        Establishment establishment = new Establishment(provider, "Campetto da basket", "via ciao 13", new Coordinates(12, 12));

        List<SlotBlueprint> blueprints = new ArrayList<>();
        for (LocalTime start = LocalTime.of(8, 0); start.compareTo(LocalTime.of(22, 0)) < 0; start = start.plusMinutes(90)) {
            Integer reservationLimit = random.nextInt(11) > 6 ? null : random.nextInt(31);

            blueprints.add(new PeriodicSlotBlueprint(
                    start, start.plusMinutes(90), establishment, reservationLimit,
                    weekdays, LocalDate.of(2022, 11, 14), LocalDate.of(2022, 11, 20)
            ));
        }

        for (SlotBlueprint instantiatedBlueprint : blueprints) {
            for (int i = 0; i < instantiatedBlueprint.toDate.compareTo(instantiatedBlueprint.fromDate); i++) {
                PeriodicSlot slot = new PeriodicSlot(LocalDate.now().plusDays(i), provider, (PeriodicSlotBlueprint) instantiatedBlueprint);

                int bookedPeopleAmount = instantiatedBlueprint.reservationLimit != null ? instantiatedBlueprint.reservationLimit : 15;
                if(random.nextInt(11) > 6)
                    bookedPeopleAmount = random.nextInt(bookedPeopleAmount + 1);
                if(bookedPeopleAmount > 0 && random.nextInt(11) > 5)
                    slot.passwordProtected = true;

                for(int j = 0; j < bookedPeopleAmount; j++)
                    slot.reservations.add(new Client(new Coordinates(1,1), "", "", "", "", LocalDate.now()));
            }
        }

        return blueprints;
    }

    private List<SlotBlueprint> selectRandom(List<SlotBlueprint> list) {
        Collections.shuffle(list);
        int start = random.nextInt(list.size());
        int len = start + random.nextInt(list.size() - start);
        return list.subList(start, len);
    }
}