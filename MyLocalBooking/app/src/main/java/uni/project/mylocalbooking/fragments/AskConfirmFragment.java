package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.app.Dialog;

public class AskConfirmFragment extends DialogFragment {
    private final String title;
    private final String question;
    private final String successTitle;
    private final String successMessage;


    public AskConfirmFragment (String title, String question, String successTitle, String successMessage){
        this.title = title;
        this.question = question;
        this.successTitle = successTitle;
        this.successMessage = successMessage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(question).setTitle(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFragment newFragment = new SuccessFragment(successTitle, successMessage);
                        newFragment.show(requireActivity().getSupportFragmentManager(), "confirm");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }
}