package uni.project.mylocalbooking.activities.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;


import java.util.ArrayList;

import uni.project.mylocalbooking.MyLocalBooking;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_list);

        viewModel = new ViewModelProvider(this).get(SlotListViewModel.class);

        SlotListAdapter adapter = new SlotListAdapter(this);
        ((ListView) findViewById(R.id.slot_list)).setAdapter(adapter);

        viewModel.getCurrentDay().observe(this, date -> {
            adapter.onRefresh(date, viewModel.getBlueprints(date));
            findViewById(R.id.no_available_slots).setVisibility(adapter.filteredSlots.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getReservationOutcome().observe(this, code -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // TODO: be more specific with error message
            builder.setMessage(R.string.reservation_error_generic_message)
                    .setTitle(R.string.reservation_error);
            builder.create();

        });

        currentEstablishment = (Establishment) getIntent().getExtras().getParcelable("current_establishment");
        viewModel.setBlueprints(currentEstablishment.blueprints);
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