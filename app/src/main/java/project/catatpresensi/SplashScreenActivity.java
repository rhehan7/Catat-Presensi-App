package project.catatpresensi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    boolean isMockEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Mengecek status login dalam shared Preference
                boolean isLoggedIn = checkLoginStatus();
                // check lokasi palsu
                boolean isMockLocation = checkMockLocation();
                // Redirect pengguna berdasarkan status login
                if (isLoggedIn) {
                    if (isMockLocation) {
                        // Lokasi palsu terdeteksi
                        startActivity(new Intent(SplashScreenActivity.this, BufferActivity.class));
                    } else {
                        // Lokasi palsu tidak terdeteksi
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    }
                } else {
                    if (isMockLocation) {
                        // Lokasi palsu terdeteksi
                        startActivity(new Intent(SplashScreenActivity.this, BufferActivity.class));
                    } else {
                        // pengguna belum login atau telah logout, arahkan ke LoginActivity
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    }
                }
                finish();
            }
        }, 2000);   // lama waktu menampilkan splash screen
    } // end of method onCreate

    private boolean checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
    private boolean checkMockLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            isMockEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                    locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            if (isMockEnabled) {
                // Lokasi palsu terdeteksi
                Toast.makeText(this, "lokasi palsu terdeteksi", Toast.LENGTH_SHORT).show();
                Log.d("MockLocation", "Lokasi palsu terdeteksi");
                return true;
            }
        }
        return false;
    }


} // end Class