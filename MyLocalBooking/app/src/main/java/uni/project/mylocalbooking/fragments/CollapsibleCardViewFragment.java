package uni.project.mylocalbooking.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
                    .add(R.id.inner_content, fragment, title)
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
        View innerContent = getView().findViewById(R.id.inner_content);
        ViewGroup parent = (ViewGroup) innerContent.getParent();
        int index = parent.indexOfChild(innerContent);
        parent.removeView(innerContent);
        innerContent = view;
        innerContent.setId(R.id.inner_content);
        parent.addView(innerContent, index);

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

    private void setExpanded(boolean expanded) {
        ((IOnAttachedListener) getParentFragment()).onFragmentAttached(title);

        getView().findViewById(R.id.inner_content).setVisibility(expanded ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.next_button).setVisibility(expanded ? View.VISIBLE : View.GONE);
    }
}