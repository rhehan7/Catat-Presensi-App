package project.catatpresensi.fragment;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import project.catatpresensi.CameraActivity;
import project.catatpresensi.R;
import project.catatpresensi.api.ApiClient;
import project.catatpresensi.model.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private AppCompatTextView textViewDate;
    private AppCompatTextView textViewYear;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault());
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    private final Handler handler = new Handler();

    private MaterialCardView btnMasuk;
    private MaterialCardView btnPulang;
    private MaterialCardView btnMulaiLembur;
    private MaterialCardView btnSelesaiLembur;
    private String jenisPresensi;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // memastikan menampilkan bottom navigation
        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
        // mengambil nama perusahaan & username
        profile(view);
//        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        String username = sharedPreferences.getString("username", "");
//        // Menampilkan username di TextView
//        AppCompatTextView tvUsername = view.findViewById(R.id.username_home);
//        tvUsername.setText(username);

        checkLocationPermission();  // Memeriksa dan meminta izin lokasi jika belum diberikan
        updateData(); // run function for renew date
        initializeViews(view); // inisialisai view
        // click button action
        btnMasuk.setOnClickListener(v -> handleLocationRequestAndNavigate("Masuk"));
        btnPulang.setOnClickListener(v -> handleLocationRequestAndNavigate("Pulang"));
        btnMulaiLembur.setOnClickListener(v -> handleLocationRequestAndNavigate("Masuk Lembur"));
        btnSelesaiLembur.setOnClickListener(v -> handleLocationRequestAndNavigate("Pulang Lembur"));

        return view;
    }

    private void profile(View view) {
        // mendapatkan token dari sharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        // membuat permintaan
        ApiClient.getClient().getDataProfile("Bearer " + token)
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            ServerResponse profileHome = response.body();
                            if (profileHome != null) {
                                String companyName = profileHome.getData().getCompanyName();
                                String username = profileHome.getData().getUsername();

                                AppCompatTextView company_name = view.findViewById(R.id.company_name);
                                AppCompatTextView user_name = view.findViewById(R.id.username_home);
                                // set Data to text view
                                company_name.setText(companyName);
                                user_name.setText(username);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Gagal memuat data" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void handleLocationRequestAndNavigate(String jenisPresensi) {
        this.jenisPresensi = jenisPresensi;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                getLocationAndNavigate(jenisPresensi);
            } else {
                // Handle permission request
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            showToast("Silahkan aktifkan GPS!");    // Show dialog to enable GPS
        }
    }
    private void getLocationAndNavigate(String jenisPresensi) {
        // Memeriksa izin lokasi
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Jika izin sudah diberikan, lanjutkan dengan pengambilan lokasi
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000); // Update location every 5 seconds

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        navigateToCameraWithLocation(jenisPresensi, location.getLatitude(), location.getLongitude());
                    }   else {
                        showToast("Tidak dapat mendapatkan lokasi saat ini.");  // Handle situation when location is null
                    }
                }
            };
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }
    private void navigateToCameraWithLocation(String jenisPresensi, double latitude, double longitude) {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        intent.putExtra("jenisPresensi", jenisPresensi);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location and navigate
                getLocationAndNavigate(jenisPresensi);
            } else {
                // Permission denied, show a message to the user
                showToast("Izin lokasi diperlukan untuk pengambilan lokasi");
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Gambar berhasil diambil dari kamera (mengambil dari intent)
            assert data != null;
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data"); // "data" adalah key default dari sistem
            // Convert imageBitmap to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            // mengambil data lain dari intent
            String jenisPresensi = data.getStringExtra("jenisPresensi");
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);
            String waktuPresensi = getTime();   // mengambil waktu saat ini

            showSuccessScreen(jenisPresensi, latitude, longitude, waktuPresensi, byteArray);
        }
    }


    private void initializeViews(View view) {
        textViewDate = view.findViewById(R.id.date_home);
        textViewYear = view.findViewById(R.id.year_home);
        btnMasuk = view.findViewById(R.id.cv_masuk);
        btnPulang = view.findViewById(R.id.cv_pulang);
        btnMulaiLembur = view.findViewById(R.id.cv_mulaiLembur);
        btnSelesaiLembur = view.findViewById(R.id.cv_selesaiLembur);
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void showSuccessScreen(String jenisPresensi, double latitude, double longitude, String waktuPresensi, byte[] byteArray) {
        // Menggunakan Bundle untuk mengirim data ke SuccessFragment
        Bundle bundle = new Bundle();
        bundle.putByteArray("imageBitmap", byteArray);
        bundle.putString("waktuPresensi", waktuPresensi);
        bundle.putString("jenisPresensi", jenisPresensi);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        // Membuat instance dari SuccessFragment dan mengirim bundle
        SuccessFragment successFragment = new SuccessFragment();
        successFragment.setArguments(bundle);
        // Memulai transaksi fragment
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, successFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd MMMM yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
    private void updateData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // renew data and time
                Calendar calendar = Calendar.getInstance();
                String currentDate = dateFormat.format(calendar.getTime());
                String currentYear = yearFormat.format(calendar.getTime());
                // menampilkan tanggal, bulan, tahun di textView
                textViewDate.setText(currentDate);
                textViewYear.setText(currentYear);
                // memperbarui setiap 1 detik
                handler.postDelayed(this, 1000);
            }
        }, 10); // delay awal sebelum memperbarui
    }
    private void showToast(String message) {
        Context context = requireContext(); // atau getContext()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}   // end class HomeFragment