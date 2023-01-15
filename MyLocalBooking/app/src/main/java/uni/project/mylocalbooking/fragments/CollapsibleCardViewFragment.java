package uni.project.mylocalbooking.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import uni.project.mylocalbooking.R;

public class CollapsibleCardViewFragment extends Fragment {
    public interface IOnAttachedListener {
        void onFragmentAttached(String title);
        void onCardViewClicked(String title);
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
        view.findViewById(R.id.cardview_header).setOnClickListener(v -> {
            ((IOnAttachedListener) getParentFragment()).onCardViewClicked(title);
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
        return view;
    }

    public void expand() {
        setExpanded(true);
    }

    public void collapse() {
        setExpanded(false);
    }

    private void toggleExpanded(View root) {
        LinearLayout layout = root.findViewById(R.id.content_layout);
        if(layout.getChildCount() == 0)
            ((IOnAttachedListener) getParentFragment()).onFragmentAttached(title);
        else {
            int visibility = layout.getVisibility();
            if(visibility == View.VISIBLE)
                collapse();
            else
                expand();
        }
    }

    private void setExpanded(boolean expanded) {
        LinearLayout layout = getView().findViewById(R.id.content_layout);
        if(layout.getChildCount() == 0)
            ((IOnAttachedListener) getParentFragment()).onFragmentAttached(title);

        getView().findViewById(R.id.content_layout).setVisibility(expanded ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.next_button).setVisibility(expanded ? View.VISIBLE : View.GONE);
    }
}