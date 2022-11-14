package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import uni.project.mylocalbooking.R;

public class SlotPasswordDialogFragment extends DialogFragment {
    public interface ISlotPasswordDialogListener {
        void onConfirm();
    }

    public SlotPasswordDialogFragment() {
        super();

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(requireActivity().getLayoutInflater().inflate(R.layout.slot_password_layout, null));
        builder.setTitle(R.string.slot_password_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    // START THE GAME!
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
