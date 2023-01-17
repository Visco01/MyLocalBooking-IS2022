package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentResultListener;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class CreateBlueprintActivity extends AppCompatActivity {
    private Establishment establishment;
    private final Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blueprint);

        establishment = getIntent().getExtras().getParcelable("establishment");
        bundle.putParcelable("establishment", establishment);

        if(establishment.blueprints.isEmpty()) {
            findViewById(R.id.manual_button).setOnClickListener(v -> {
                startManual();
            });
            findViewById(R.id.periodic_button).setOnClickListener(v -> {
                startPeriodic();
            });
            return;
        }

        findViewById(R.id.buttons_layout).setVisibility(View.GONE);

        boolean isPeriodic = establishment.blueprints.stream().findAny().get() instanceof PeriodicSlotBlueprint;

        getSupportFragmentManager().setFragmentResultListener("blueprint", this, (requestKey, bundle) -> {
            SlotBlueprint result = bundle.getParcelable("blueprint");
            IMyLocalBookingAPI.getApiInstance().addBlueprint(result, blueprint -> {
                System.out.println();
            }, code -> {
                System.out.println();
            });
            finish();
        });

        if(isPeriodic)
            startPeriodic();
        else
            startManual();
    }

    private void startPeriodic() {
        findViewById(R.id.buttons_layout).setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.blueprint_creation_fragment, PeriodicBlueprintCreationFragment.class, bundle)
                .commit();
    }

    private void startManual() {
        findViewById(R.id.buttons_layout).setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.blueprint_creation_fragment, ManualBlueprintCreationFragment.class, bundle)
                .commit();
    }
}