package project.catatpresensi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import project.catatpresensi.fragment.HistoryFragment;
import project.catatpresensi.fragment.HomeFragment;
import project.catatpresensi.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hide action bar
        if (getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }
        // tampilan awal container pada MainActivity
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        // bind atribute
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // click item's bottomNav action
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                case R.id.history:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, historyFragment).commit();
                    return true;
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                    return true;
            }

            return false;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFragment())
                    .commit();
        }
    }   // end onCreate


}   // end class MainActivity