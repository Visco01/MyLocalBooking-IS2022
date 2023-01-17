package uni.project.mylocalbooking.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uni.project.mylocalbooking.R;

public class CollapsibleCardViewFragment extends Fragment {
    public interface ICollapsibleCardViewParent {
        void onCardViewClicked(String title);
    }

    public final String title;
    private final View.OnClickListener listener;
    public Fragment innerFragment;
    public View innerView;

    private CollapsibleCardViewFragment(String title, View.OnClickListener listener) {
        this.listener = listener;
        this.title = title;
    }

    public CollapsibleCardViewFragment(String title, Fragment innerFragment, View.OnClickListener listener) {
        this(title, listener);
        this.innerFragment = innerFragment;
    }

    public CollapsibleCardViewFragment(String title, View innerView, View.OnClickListener listener) {
        this(title, listener);
        this.innerView = innerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collapsible_cardview, container, false);
        ((TextView) view.findViewById(R.id.cardview_title)).setText(title);
        view.findViewById(R.id.cardview_header).setOnClickListener(v -> {
            ((ICollapsibleCardViewParent) getParentFragment()).onCardViewClicked(title);
        });

        if(innerFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.inner_content, innerFragment, title)
                    .commit();
        }
        else {
            View innerContent = view.findViewById(R.id.inner_content);
            ViewGroup parent = (ViewGroup) innerContent.getParent();
            int index = parent.indexOfChild(innerContent);
            parent.removeView(innerContent);
            innerView.setId(R.id.inner_content);
            parent.addView(innerView, index);
        }

        view.findViewById(R.id.next_button).setOnClickListener(listener);
        return view;
    }

    public void expand() {
        setExpanded(true);
    }

    public void collapse() {
        setExpanded(false);
    }

    private void setExpanded(boolean expanded) {
        getView().findViewById(R.id.inner_content).setVisibility(expanded ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.next_button).setVisibility(expanded ? View.VISIBLE : View.GONE);
    }
}