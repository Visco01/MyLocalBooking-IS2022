package uni.project.mylocalbooking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.function.Consumer;

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
            if(linearLayout.getChildCount() == 0)
                ((IOnAttachedListener) getParentFragment()).notifyFragmentAttached(title);
            else
                toggleExpanded(view);
        });
        return view;
    }

    public <T extends Fragment> T setContent(Class<T> fragmentClass, Bundle bundle, View.OnClickListener listener) {
        try {
            T fragment = fragmentClass.newInstance();
            fragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.content_layout, fragment, title)
                    .commit();

            AppCompatButton button = (AppCompatButton) getView().findViewById(R.id.next_button);
            button.setOnClickListener(listener);
            button.setVisibility(View.VISIBLE);
            return fragment;
        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T extends View> T setContent(T view, View.OnClickListener listener) {
        ((LinearLayout) getView().findViewById(R.id.content_layout)).addView(view);

        AppCompatButton button = (AppCompatButton) getView().findViewById(R.id.next_button);
        button.setOnClickListener(listener);
        button.setVisibility(View.VISIBLE);
        return view;
    }

    public void expand() {
        setExpanded(true);
    }

    public void collapse() {
        setExpanded(false);
    }

    private void toggleExpanded(View root) {
        int visibility = root.findViewById(R.id.content_layout).getVisibility();
        if(visibility == View.VISIBLE)
            collapse();
        else
            expand();
    }

    private void setExpanded(boolean expanded) {
        getView().findViewById(R.id.content_layout).setVisibility(expanded ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.next_button).setVisibility(expanded ? View.VISIBLE : View.GONE);
    }
}