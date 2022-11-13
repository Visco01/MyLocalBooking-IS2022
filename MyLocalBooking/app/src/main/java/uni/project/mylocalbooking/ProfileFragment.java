package uni.project.mylocalbooking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uni.project.mylocalbooking.databinding.FragmentProfileBinding;

import uni.project.mylocalbooking.mockdata.User;

/**
 * Profile fragment
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private User user = new User ();

    public ProfileFragment() {
        // Required empty public constructor
    }

      public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Binding Operations
        binding.userFullName.setText(user.fullName());
        binding.userName.setText(user.getName());
        binding.userLastname.setText(user.getLastname());
        binding.userAge.setText(user.getAge().toString());
        //binding.userAddress.setText(user.getAddress());
        binding.userAddress.setText(user.getLongAddress());
        binding.userCellNumber.setText("Cell: " + user.getCellNumber());
        binding.userEmail.setText("Email: " + user.getEmail());

        // Danger Age
        if (user.getAge() > 18){
            binding.dangerAge.setVisibility(View.INVISIBLE);
            System.out.println("Utente Maggiorenne");
        }
        else{
            System.out.println("Utente Minorenne, Attenzione");
        }

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