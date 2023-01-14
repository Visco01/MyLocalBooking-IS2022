package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.CollapsibleCardViewFragment;
import uni.project.mylocalbooking.fragments.WeekdayPickerFragment;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class BlueprintCreationFragment extends Fragment implements CollapsibleCardViewFragment.IOnAttachedListener {
    private Collection<SlotBlueprint> blueprints;
    protected final HashMap<String, CollapsibleCardViewFragment> cardViewFragments = new HashMap<>();
    protected final HashMap<String, Class<? extends Fragment>> innerFragments = new HashMap<>();
    protected final HashMap<String, Bundle> innerBundles = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blueprints = Arrays.stream(getArguments().getParcelableArray("blueprints"))
                .map(b -> (SlotBlueprint) b)
                .collect(Collectors.toList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blueprint_creation, container, false);
        LinearLayout list = view.findViewById(R.id.blueprint_creation_steps_layout);
        addWeekdays(list);
        return view;
    }

    private void addWeekdays(LinearLayout list) {
        FragmentContainerView fragmentContainer = new FragmentContainerView(getContext());
        fragmentContainer.setId(View.generateViewId());
        list.addView(fragmentContainer);

        String title = "Weekdays";
        Bundle bundle = new Bundle();
        bundle.putString("title", title);

        CollapsibleCardViewFragment fragment = new CollapsibleCardViewFragment();
        fragment.setArguments(bundle);
        cardViewFragments.put(title, fragment);
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(fragmentContainer.getId(), fragment)
                .commit();

        innerFragments.put(title, WeekdayPickerFragment.class);
    }

    @Override
    public void notifyFragmentAttached(String title) {
        cardViewFragments.get(title).setContent (
                innerFragments.get(title),
                innerBundles.get(title)
        );
    }
}