package uni.project.mylocalbooking.mirco;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uni.project.mylocalbooking.databinding.FragmentHomeBinding;
import uni.project.mylocalbooking.mirco.mockdata.Establishment;
import uni.project.mylocalbooking.mirco.mockdata.User;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Establishment est = new Establishment();
    private User user = new User();
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Binding operations
        binding.establishmentAddress.setText(est.address);
        binding.establishmentName.setText(est.name);
        binding.establishmentOwner.setText(est.owner.fullName());
        binding.reviewScore.setText("3.8/5.0");
        binding.price.setText(est.price + "€ per slot");

        binding.userFullName.setText(user.fullName());

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