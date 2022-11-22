package uni.project.mylocalbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    HomeProviderFragment homeProviderFragment = new HomeProviderFragment();
    ProfileProviderFragment profileProviderFragment = new ProfileProviderFragment();
    //private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----- Navigation ----- //
        /* TO-DO:
        * Depending on the AppUser.type, set the right icons and paths on the
        * bottomNav bar.
        * E.g:
        * A Client will have in his bottom bar [Home, Profile, My Reservations]
        * Else a Provider will have [Home, Profile, My Establishments]
        * */
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, homeFragment).commit();
                    return true;
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, profileFragment).commit();
                    return true;
                // Only for testing!!!
                case R.id.homeProvider:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, homeProviderFragment).commit();
                    return true;
                case R.id.profileProvider:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, profileProviderFragment).commit();
                    return true;
                /*
                * case R.id.myReservations:
                * cas R.id.myEstablishments:
                * */
            }
            return false;
        });
        // Default position
        bottomNavigationView.setSelectedItemId(R.id.home);
        // ----- End of Navigation ----- //

    }

}