package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.ISelectableSlot;

public class SetSlotPasswordDialogFragment extends DialogFragment {
    public interface IListener {
        void onAccepted(ISelectableSlot slot);
        void onRefused(ISelectableSlot slot);
    }

    private final IListener listener;
    private final ISelectableSlot slot;

    public SetSlotPasswordDialogFragment(IListener listener, ISelectableSlot slot) {
        super();
        this.listener = listener;
        this.slot = slot;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.set_slot_password_title)
                .setMessage(R.string.question_set_slot_password)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    listener.onAccepted(slot);
                })
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    listener.onRefused(slot);
                });
        return builder.create();
    }
}
