package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import uni.project.mylocalbooking.R;

public class PasswordRequestDialogFragment extends DialogFragment {
    public interface IListener {
        void onAccepted();
        void onRefused();
    }

    private final IListener listener;

    public PasswordRequestDialogFragment(IListener listener) {
        super();
        this.listener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.set_slot_password_title)
                .setMessage(R.string.question_set_slot_password)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    listener.onAccepted();
                })
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    listener.onRefused();
                });
        return builder.create();
    }
}
