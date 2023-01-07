package uni.project.mylocalbooking.activities.provider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.activities.ChangeDataActivity;
import uni.project.mylocalbooking.activities.ChangeNameActivity;
import uni.project.mylocalbooking.activities.ChangePasswordActivity;

public class EditChoiceDialogProviderActivity extends Dialog implements View.OnClickListener{

    private Button psswd, birthday, nstrikes, name;
    private Activity motherActivity;

    public EditChoiceDialogProviderActivity(Activity A) {
        super(A);
        motherActivity = A;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_choice_dialog_provider);

        psswd = (Button) findViewById(R.id.editPassword);
        birthday = (Button) findViewById(R.id.editBirthday);
        nstrikes = (Button) findViewById(R.id.editNStrikes);
        name = (Button) findViewById(R.id.editName);

        psswd.setOnClickListener(this);
        birthday.setOnClickListener(this);
        nstrikes.setOnClickListener(this);
        name.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editPassword:
                Intent intent = new Intent(motherActivity, ChangePasswordActivity.class);
                motherActivity.startActivity(intent);
                return;
            case R.id.editBirthday:
                motherActivity.startActivity(new Intent(motherActivity, ChangeDataActivity.class));
                return;
            case R.id.editNStrikes:
                motherActivity.startActivity(new Intent(motherActivity, ChangeNStrikesActivity.class));
                return;
            case R.id.editName:
                motherActivity.startActivity(new Intent(motherActivity, ChangeNameActivity.class));
                return;
            default:
                dismiss();
        }
    }

}