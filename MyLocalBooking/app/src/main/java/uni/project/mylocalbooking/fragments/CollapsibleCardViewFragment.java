package uni.project.mylocalbooking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import uni.project.mylocalbooking.R;

public class CollapsibleCardViewFragment extends Fragment {
    public interface IOnAttachedListener {
        void notifyFragmentAttached(String title);
    }

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
        LinearLayout linearLayout = view.findViewById(R.id.content_layout);
        view.findViewById(R.id.base_cardview).setOnClickListener(v -> {
            if(linearLayout.getChildCount() <= 1)
                ((IOnAttachedListener) getParentFragment()).notifyFragmentAttached(title);
            else {
                for(int i = 1; i < linearLayout.getChildCount(); i++) {
                    View content = linearLayout.getChildAt(i);
                    content.setVisibility(content.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                }
            }
        });
        return view;
    }

    public Fragment setContent(Class<? extends Fragment> fragmentClass, Bundle bundle) {
        try {
            Fragment fragment = fragmentClass.newInstance();
            fragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.content_layout, fragment)
                    .commit();
            return fragment;
        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
}