package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.ISelectableSlot;

public class SlotPasswordDialogFragment extends DialogFragment {
    public interface IListener {
        void onSlotPasswordSubmitted(ISelectableSlot slot, String password);
    }

    private final IListener listener;
    private final ISelectableSlot slot;

    public SlotPasswordDialogFragment(IListener listener, ISelectableSlot slot) {
        super();
        this.listener = listener;
        this.slot = slot;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.slot_password_layout, null);
        builder.setView(view);
        builder.setTitle(R.string.slot_password_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    listener.onSlotPasswordSubmitted(slot, ((EditText) view.findViewById(R.id.slot_password_field)).getText().toString());
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }
}
