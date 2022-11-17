package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.ISelectableSlot;

public class ProvideSlotPasswordDialogFragment extends DialogFragment {
    public interface IConfirmListener {
        void onSlotPasswordSubmitted(String password);
    }
    public interface ICancelListener {
        void onSlotPasswordCanceled();
    }

    private final IConfirmListener confirmListener;
    private final ICancelListener cancelListener;
    private final int titleId;

    public ProvideSlotPasswordDialogFragment(IConfirmListener confirmListener, ICancelListener cancelListener, int titleId) {
        super();
        this.confirmListener = confirmListener;
        this.cancelListener = cancelListener;
        this.titleId = titleId;
    }

    public ProvideSlotPasswordDialogFragment(IConfirmListener confirmListener, int titleId) {
        this(confirmListener, null, titleId);
    }

    public ProvideSlotPasswordDialogFragment(ICancelListener cancelListener, int titleId) {
        this(null, cancelListener, titleId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.input_slot_password_layout, null);
        builder.setView(view);
        builder.setTitle(titleId)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    confirmListener.onSlotPasswordSubmitted(((EditText) view.findViewById(R.id.slot_password_field)).getText().toString());
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> cancelListener.onSlotPasswordCanceled());
        return builder.create();
    }
}
