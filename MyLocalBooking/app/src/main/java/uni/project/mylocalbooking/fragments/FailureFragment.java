package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;

public class FailureFragment extends DialogFragment {
    private final String errorMessage;
    private final String errorTitle;
    // atm unused
    private final boolean goHome = false;

    public FailureFragment (String title, String message){
        errorTitle = title;
        errorMessage = message;
    }

    // To help with critical errors that require an Activity to be re-opened
    /*
    public FailureFragment (String title, String message, boolean home){
        errorTitle = title;
        errorMessage = message;
        goHome = true;
    }
    */

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(errorMessage).setTitle(errorTitle)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    // Atm will never go inside the if
                    public void onClick(DialogInterface dialog, int id) {
                        if (goHome){
                            startActivity(new Intent(MyLocalBooking.getAppContext(), HomeProviderActivity.class));
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // Atm will never go inside the if
        if (goHome){
            startActivity(new Intent(MyLocalBooking.getAppContext(), HomeProviderActivity.class));
        }
    }
}