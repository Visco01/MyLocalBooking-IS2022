package uni.project.mylocalbooking.mirco;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uni.project.mylocalbooking.databinding.FragmentProfileBinding;
<<<<<<< Updated upstream:MyLocalBooking/app/src/main/java/com/example/mylocalbooking/ProfileFragment.java
import uni.project.mylocalbooking.mockdata.User;
=======

import uni.project.mylocalbooking.mirco.mockdata.User;
>>>>>>> Stashed changes:MyLocalBooking/app/src/main/java/com/example/mylocalbooking/mirco/ProfileFragment.java

/**
 * A simple {@link Fragment} subclass that represent the profile.
 */
public class ProfileFragment extends Fragment {

    // Binding
    private FragmentProfileBinding binding;

    // ----- MOCK DATA PART ----- //
    User user = new User();
    // ----- End of MOCK DATA PART ----- //

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        //View view = binding.getRoot();
        //return inflater.inflate(R.layout.fragment_profile, container, false);
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Operazioni onCreate
        binding.userFullName.setText(user.fullName());
        binding.userName.setText(user.getName());
        binding.userLastname.setText(user.getLastname());
        binding.userAge.setText(user.getAge().toString());
        //binding.userAddress.setText(user.getAddress());
        binding.userAddress.setText(user.getLongAddress());
        binding.userCellNumber.setText(user.getCellNumber());
        binding.userEmail.setText(user.getEmail());

        // Danger Age
        if (user.getAge() > 18){
            binding.dangerAge.setVisibility(View.INVISIBLE);
            System.out.println("Utente Maggiorenne");
        }
        else{
            System.out.println("Utente Minorenne, Attenzione");
        }
        binding.dangerAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //binding.;
                System.out.println("Click");
            }
        });


        // Codice di return
        View view = binding.getRoot();
        System.out.println("! VIEW CREATED !");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        System.out.println("! VIEW DESTROYED !");
    }


}