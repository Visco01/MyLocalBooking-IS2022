package uni.project.mylocalbooking.activities.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.PasswordInputDialogFragment;
import uni.project.mylocalbooking.fragments.PasswordRequestDialogFragment;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListActivity extends AppCompatActivity implements SlotListAdapter.IListener {
    private SlotListViewModel viewModel;
    private Establishment currentEstablishment;
    private final SlotListAdapter adapter = new SlotListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentEstablishment = (Establishment) getIntent().getExtras().getParcelable("current_establishment");
        setContentView(R.layout.activity_slot_list);

        viewModel = new ViewModelProvider(this).get(SlotListViewModel.class);
        ((ListView) findViewById(R.id.slot_list)).setAdapter(adapter);

        ((SwipeRefreshLayout) findViewById(R.id.swiperefresh)).setOnRefreshListener(() -> {
            refreshDate(viewModel.getCurrentDay().getValue());
        });

        viewModel.getCurrentDay().observe(this, this::refreshDate);

        viewModel.getReservationOutcome().observe(this, code -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // TODO: be more specific with error message
            builder.setMessage(R.string.make_reservation_error_generic_message)
                    .setTitle(R.string.reservation_error);
            builder.create();

        });
    }

    private void refreshDate (LocalDate date) {
        new Thread(() -> {
            try {
                Collection<SlotBlueprint> blueprints = currentEstablishment.getBlueprints(date);
                adapter.onRefresh(date, blueprints);

                findViewById(R.id.reservations_warning_text).setVisibility(blueprints.isEmpty() ? View.VISIBLE : View.GONE);
                if(blueprints.isEmpty())
                    ((TextView) findViewById(R.id.reservations_warning_text)).setText(R.string.no_available_slots);
            } catch (Establishment.PartialReservationsResultsException e) {
                e.printStackTrace();
                findViewById(R.id.reservations_warning_text).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.reservations_warning_text)).setText(R.string.get_reservation_error_generic_message);
            }
        }).start();
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
                    viewModel.makeReservation(selectableSlot, null);
                }
            });
            dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
        } else if(selectableSlot.isPasswordProtected()) {
            showPasswordInputDialog(selectableSlot, R.string.slot_password_required, null);
        }
        else {
            viewModel.makeReservation(selectableSlot, null);
        }
    }

    @Override
    public void onManualSlotCreate(ManualSlotCreationDialogFragment.FreeManualTimeWindow timeWindow) {
        ManualSlotCreationDialogFragment slotCreationDialog = new ManualSlotCreationDialogFragment(timeWindow, newTimeWindow -> {
            ManualSlot slot = new ManualSlot(
                    newTimeWindow.fromTime,
                    newTimeWindow.toTime,
                    newTimeWindow.date,
                    null,
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
                viewModel.makeReservation(slot, null);
            }
        });
        wishToSetPasswordDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private void showPasswordInputDialog(ISelectableSlot slot, int titleId, PasswordInputDialogFragment.ICancelListener cancelListener) {
        PasswordInputDialogFragment dialog = new PasswordInputDialogFragment(password -> viewModel.makeReservation(slot, password), cancelListener, titleId);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }
}