package uni.project.mylocalbooking.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.app.Dialog;

import java.util.Objects;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.SessionPreferences;
import uni.project.mylocalbooking.activities.client.HomeClientActivity;
import uni.project.mylocalbooking.activities.provider.HomeProviderActivity;

public class SuccessFragment extends DialogFragment {
    private final String successTitle;
    private final String successMessage;

    public SuccessFragment (String title, String message){
        successTitle = title;
        successMessage = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(successMessage).setTitle(successTitle)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // User type dynamic
        getActivity().finish();
    }
}