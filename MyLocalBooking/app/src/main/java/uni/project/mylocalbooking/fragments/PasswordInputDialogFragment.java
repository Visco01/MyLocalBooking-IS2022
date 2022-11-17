package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import uni.project.mylocalbooking.R;

public class PasswordInputDialogFragment extends DialogFragment {
    public interface IConfirmListener {
        void onSlotPasswordSubmitted(String password);
    }
    public interface ICancelListener {
        void onSlotPasswordCanceled();
    }

    private final IConfirmListener confirmListener;
    private final ICancelListener cancelListener;
    private final int titleId;

    public PasswordInputDialogFragment(IConfirmListener confirmListener, ICancelListener cancelListener, int titleId) {
        super();
        this.confirmListener = confirmListener;
        this.cancelListener = cancelListener;
        this.titleId = titleId;


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = requireActivity().getLayoutInflater().inflate(R.layout.input_slot_password_layout, null);
        DialogInterface.OnClickListener confirmCallback = confirmListener == null ? null : (d,i) -> {
            confirmListener.onSlotPasswordSubmitted(((EditText) view.findViewById(R.id.slot_password_field)).getText().toString());
        };
        DialogInterface.OnClickListener cancelCallback = cancelListener == null ? null : (d,i) -> cancelListener.onSlotPasswordCanceled();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(titleId)
                .setPositiveButton(R.string.confirm, confirmCallback)
                .setNegativeButton(R.string.cancel, cancelCallback);
        return builder.create();
    }
}
