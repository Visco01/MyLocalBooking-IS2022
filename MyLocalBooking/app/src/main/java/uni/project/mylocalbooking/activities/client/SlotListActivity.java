package uni.project.mylocalbooking.activities.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.ProvideSlotPasswordDialogFragment;
import uni.project.mylocalbooking.fragments.SetSlotPasswordDialogFragment;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListActivity extends AppCompatActivity implements SlotListAdapter.IListener {
    private SlotListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_list);

        viewModel = new ViewModelProvider(this).get(SlotListViewModel.class);

        // TODO: get blueprints
        List<SlotBlueprint> blueprints = new ArrayList<>();

        viewModel.setBlueprints(blueprints);

        SlotListAdapter adapter = new SlotListAdapter(this);

        viewModel.getCurrentDay().observe(this, dow -> {
            LocalDate weekStart = viewModel.getStartOfWeek().getValue();
            LocalDate selected = weekStart.plusDays(dow.getValue() - 1);
            List<SlotBlueprint> slotItems = viewModel.getBlueprints(selected);

            adapter.onRefresh(selected, slotItems);
        });

        ((ListView) getWindow().getDecorView().getRootView().findViewById(R.id.slot_list)).setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onSlotReservationToggled(ISelectableSlot selectableSlot) {
        if(selectableSlot instanceof SlotBlueprint) {
            SetSlotPasswordDialogFragment dialog = new SetSlotPasswordDialogFragment(new SetSlotPasswordDialogFragment.IListener() {
                @Override
                public void onAccepted() {
                    showPasswordInputDialog(selectableSlot, R.string.choose_slot_password, null);
                }

                @Override
                public void onRefused() {
                    makeReservation(selectableSlot, null);
                }
            });
            dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
        } else if(selectableSlot.isPasswordProtected()) {
            showPasswordInputDialog(selectableSlot, R.string.slot_password_required, null);
        }
        else {
            makeReservation(selectableSlot, null);
        }
    }

    @Override
    public void onManualSlotCreate(CreateManualSlotDialog.FreeManualTimeWindow timeWindow) {
        CreateManualSlotDialog slotCreationDialog = new CreateManualSlotDialog(timeWindow, newTimeWindow -> {
            ManualSlot slot = new ManualSlot(
                    newTimeWindow.fromTime,
                    newTimeWindow.toTime,
                    newTimeWindow.date,
                    null, // TODO: owner!
                    newTimeWindow.blueprint
            );
            requestPasswordConfirmation(slot, timeWindow);
        });
        slotCreationDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private void requestPasswordConfirmation(ManualSlot slot, CreateManualSlotDialog.FreeManualTimeWindow timeWindow) {
        SetSlotPasswordDialogFragment wishToSetPasswordDialog = new SetSlotPasswordDialogFragment(new SetSlotPasswordDialogFragment.IListener() {
            @Override
            public void onAccepted() {
                showPasswordInputDialog(slot, R.string.choose_slot_password, () -> requestPasswordConfirmation(slot, timeWindow));
            }

            @Override
            public void onRefused() {
                makeReservation(slot, null);
            }
        });
        wishToSetPasswordDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private void makeReservation(Object slot, String password) {
        // TODO: rethink interface system
    }

    private void showPasswordInputDialog(ISelectableSlot slot, int titleId, ProvideSlotPasswordDialogFragment.ICancelListener cancelListener) {
        ProvideSlotPasswordDialogFragment dialog = new ProvideSlotPasswordDialogFragment(password -> makeReservation(slot, password), cancelListener, titleId);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }
}