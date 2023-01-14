package uni.project.mylocalbooking.activities.provider;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
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
    private static class FragmentInfo<T extends Fragment> {
        private final CollapsibleCardViewFragment cardViewFragment;
        private final Class<T> innerFragmentType;
        private final Bundle bundle;

        private FragmentInfo(CollapsibleCardViewFragment cardViewFragment, Class<T> innerFragmentType, Bundle bundle) {
            this.cardViewFragment = cardViewFragment;
            this.innerFragmentType = innerFragmentType;
            this.bundle = bundle;
        }

        private T create(View.OnClickListener listener) {
            return cardViewFragment.setContent(innerFragmentType, bundle, listener);
        }

        private void setOnNext (View.OnClickListener listener) {

        }
    }
    private Collection<SlotBlueprint> blueprints;
    protected final HashMap<String, FragmentInfo> pendingFragments = new HashMap<>();
    protected final HashMap<String, Fragment> createdFragments = new HashMap<>();


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

        CollapsibleCardViewFragment fragment = new CollapsibleCardViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);

        Bundle innerBundle = new Bundle();
        innerBundle.putBoolean("simple", true);

        pendingFragments.put(title, new FragmentInfo(fragment, WeekdayPickerFragment.class, innerBundle));
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(fragmentContainer.getId(), fragment)
                .commit();
    }

    @Override
    public void notifyFragmentAttached(String title) {
        Fragment created = pendingFragments.get(title).create(v -> {
            System.out.println();
        });
        createdFragments.put(title, created);
    }
}