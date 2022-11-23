package uni.project.mylocalbooking.mirco;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uni.project.mylocalbooking.databinding.FragmentHomeProviderBinding;


public class HomeProviderFragment extends Fragment {

    private FragmentHomeProviderBinding binding;

    public HomeProviderFragment() {
        // Required empty public constructor
    }

    public static HomeProviderFragment newInstance(String param1, String param2) {
        HomeProviderFragment fragment = new HomeProviderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = uni.project.mylocalbooking.databinding.FragmentHomeProviderBinding.inflate(inflater, container, false);

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