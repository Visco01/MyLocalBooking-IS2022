package uni.project.mylocalbooking.mirco;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uni.project.mylocalbooking.databinding.FragmentProfileProviderBinding;

public class ProfileProviderFragment extends Fragment {

    private FragmentProfileProviderBinding binding;

    public ProfileProviderFragment() {
        // Required empty public constructor
    }

    public static ProfileProviderFragment newInstance(String param1, String param2) {
        ProfileProviderFragment fragment = new ProfileProviderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileProviderBinding.inflate(inflater, container, false);
        // Return code
        View view = binding.getRoot();
        System.out.println("! VIEW CREATED !");
        return view;
    }

    // Important for navigation, do not remove!
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        System.out.println("! VIEW DESTROYED !");
    }
}