package uni.project.mylocalbooking.activities.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.widget.ListView;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;

public class CreateBlueprintActivity extends AppCompatActivity {
    private Establishment establishment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blueprint);

        establishment = getIntent().getExtras().getParcelable("establishment");

        if(establishment.blueprints.isEmpty()) {
            // TODO: display choice between manual or periodic
            return;
        }

        boolean isPeriodic = establishment.blueprints.stream().findAny().get() instanceof PeriodicSlotBlueprint;
        if(!isPeriodic) {
            ManualSlotBlueprint[] arr = new ManualSlotBlueprint[establishment.blueprints.size()];
            Bundle bundle = new Bundle();
            bundle.putParcelableArray("blueprints", establishment.blueprints.toArray(arr));
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.blueprint_creation_fragment, ManualBlueprintCreationFragment.class, bundle)
                    .commit();
        }
    }
}