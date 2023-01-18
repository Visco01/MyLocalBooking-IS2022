package uni.project.mylocalbooking.activities.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import java.time.LocalDate;
import java.util.Collection;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.api.StatusCode;
import uni.project.mylocalbooking.fragments.PasswordInputDialogFragment;
import uni.project.mylocalbooking.fragments.PasswordRequestDialogFragment;
import uni.project.mylocalbooking.fragments.WeekdayPickerFragment;
import uni.project.mylocalbooking.fragments.WeekdayPickerViewModel;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListActivity extends AppCompatActivity implements SlotListAdapter.IListener {
    private WeekdayPickerViewModel weekdayPickerViewModel;
    private Establishment currentEstablishment;
    private final SlotListAdapter adapter = new SlotListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_list);

        weekdayPickerViewModel = new ViewModelProvider(this).get(WeekdayPickerViewModel.class);

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("simple", false);
            bundle.putInt(WeekdayPickerFragment.TEXT_COLOR_ARG, 0xFFFFFFFF);
            bundle.putInt(WeekdayPickerFragment.ACTIVE_TEXT_COLOR_ARG, 0xFF000000);
            bundle.putInt(WeekdayPickerFragment.BACKGROUND_COLOR_ARG, 0xFF4777b4);
            bundle.putInt(WeekdayPickerFragment.ACTIVE_BACKGROUND_COLOR_ARG, 0xFFFFFFFF);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.weekday_picker_container_view, WeekdayPickerFragment.class, bundle)
                    .commit();
        }

        currentEstablishment = (Establishment) getIntent().getExtras().getParcelable("current_establishment");

        ((ListView) findViewById(R.id.slot_list)).setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(() -> {
            loadDate(weekdayPickerViewModel.getSelectedDate().getValue(), true, () -> refreshLayout.setRefreshing(false));
        });

        weekdayPickerViewModel.getSelectedDate().observe(this, date -> {
            loadDate(date, false);
        });
    }

    private void loadDate(LocalDate date, boolean refresh, Runnable callback) {
        MutableLiveData<Collection<SlotBlueprint>> blueprints = new MutableLiveData<>();
        blueprints.observe(this, bp -> {
            adapter.onRefresh(date, bp);

            findViewById(R.id.reservations_warning_text).setVisibility(bp.isEmpty() ? View.VISIBLE : View.GONE);
            if(bp.isEmpty())
                ((TextView) findViewById(R.id.reservations_warning_text)).setText(R.string.no_available_slots);
        });

        new Thread(() -> {
            try {
                blueprints.postValue(currentEstablishment.getBlueprints(date, refresh));
            } catch (Establishment.PartialReservationsResultsException e) {
                e.printStackTrace();
                findViewById(R.id.reservations_warning_text).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.reservations_warning_text)).setText(R.string.get_reservation_error_generic_message);
            }
            if(callback != null) callback.run();
        }).start();
    }

    private void loadDate(LocalDate date, boolean refresh) {
        loadDate(date, refresh, null);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onSlotReservationToggled(ISelectableSlot selectableSlot) {
        if(selectableSlot instanceof SlotBlueprint) {
            PasswordRequestDialogFragment dialog = new PasswordRequestDialogFragment(new PasswordRequestDialogFragment.IListener() {
                @Override
                public void onAccepted() {
                    showPasswordInputDialog(selectableSlot, R.string.choose_slot_password, () -> onSlotReservationToggled(selectableSlot));
                }

                @Override
                public void onRefused() {
                    makeReservation((PeriodicSlotBlueprint) selectableSlot, null);
                }
            });
            dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
        } else {
            Slot slot = (Slot) selectableSlot;

            if(slot.reservations.contains((Client) MyLocalBooking.getCurrentUser()))
                deleteReservation(slot);
            else if(selectableSlot.isPasswordProtected())
                showPasswordInputDialog(selectableSlot, R.string.slot_password_required, null);
            else
                makeReservation(selectableSlot, null);
        }
    }

    @Override
    public void onManualSlotCreate(ManualSlotCreationDialogFragment.FreeManualTimeWindow timeWindow) {
        ManualSlotCreationDialogFragment slotCreationDialog = new ManualSlotCreationDialogFragment(timeWindow, newTimeWindow -> {
            ManualSlot slot = new ManualSlot(
                    newTimeWindow.fromTime,
                    newTimeWindow.toTime,
                    newTimeWindow.date,
                    MyLocalBooking.getCurrentUser().cellphone,
                    newTimeWindow.blueprint
            );
            requestPasswordConfirmation(slot);
        });
        slotCreationDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private void requestPasswordConfirmation(ManualSlot slot) {
        PasswordRequestDialogFragment wishToSetPasswordDialog = new PasswordRequestDialogFragment(new PasswordRequestDialogFragment.IListener() {
            @Override
            public void onAccepted() {
                showPasswordInputDialog(slot, R.string.choose_slot_password, () -> requestPasswordConfirmation(slot));
            }

            @Override
            public void onRefused() {
                makeReservation((Slot) slot, null);
            }
        });
        wishToSetPasswordDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private void showPasswordInputDialog(ISelectableSlot slot, int titleId, PasswordInputDialogFragment.ICancelListener cancelListener) {
        PasswordInputDialogFragment dialog = new PasswordInputDialogFragment(password -> makeReservation(slot, password), cancelListener, titleId);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private void deleteReservation(Slot slot) {
        IMyLocalBookingAPI.getApiInstance().cancelReservation(slot, s -> {
            loadDate(weekdayPickerViewModel.getSelectedDate().getValue(), true);
        }, null);
    }

    private void makeReservation(ISelectableSlot selectable, String password) {
        if(selectable instanceof Slot)
            makeReservation((Slot) selectable, password);
        else
            makeReservation((PeriodicSlotBlueprint) selectable, password);
    }

    private void makeReservation(Slot slot, String password) {
        if(slot instanceof PeriodicSlot)
            slot.setOwner(slot.blueprint.establishment.getProviderCellphone());
        else
            slot.setOwner(MyLocalBooking.getCurrentUser());

        IMyLocalBookingAPI.getApiInstance().addReservation(slot, password, s -> {
                    slot.reservations.add((Client) MyLocalBooking.getCurrentUser());
                    loadDate(weekdayPickerViewModel.getSelectedDate().getValue(), false);
                    onReservationOutcome(null);
                },
                code -> {
                    onReservationOutcome(code);
                });
    }

    private void makeReservation(PeriodicSlotBlueprint blueprint, String password) {
        makeReservation((Slot) new PeriodicSlot(weekdayPickerViewModel.getSelectedDate().getValue(), blueprint.establishment.getProviderCellphone(), blueprint), password);
    }

    private void onReservationOutcome(StatusCode code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // TODO: be more specific with error message
        builder.setMessage(R.string.make_reservation_error_generic_message)
                .setTitle(R.string.reservation_error);
        builder.create();
    }
}