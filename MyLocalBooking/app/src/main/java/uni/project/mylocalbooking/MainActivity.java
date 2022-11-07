package uni.project.mylocalbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    //private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----- Navigation ----- //
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, homeFragment).commit();
                    return true;
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, profileFragment).commit();
                    return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
        // ----- End of Navigation ----- //

    }

}