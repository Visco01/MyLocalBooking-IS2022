package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class Establishment_information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_information);
        Establishment establishment = getIntent().getExtras().getParcelable("establishment");
        findViewById(R.id.create_new_blueprint_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateBlueprintActivity.class).putExtra("establishment", establishment);
            startActivity(intent);
        });
    }
}