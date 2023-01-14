package uni.project.mylocalbooking.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import uni.project.mylocalbooking.R;

public class CollapsibleCardViewFragment extends Fragment {
    private String title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        title = args.getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collapsible_cardview, container, false);
        ((TextView) view.findViewById(R.id.cardview_title)).setText(title);
        return view;
    }

    public Fragment setContent(Class<? extends Fragment> fragmentClass, int resId, Bundle bundle) {
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(resId, fragmentClass, bundle)
                .commit();

        Fragment fragment = getChildFragmentManager().findFragmentById(resId);

        ((LinearLayout) getView().findViewById(R.id.content_layout)).addView(fragment.getView());

        return fragment;
    }
}