package project.catatpresensi.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import project.catatpresensi.R;
import project.catatpresensi.api.ApiClient;
import project.catatpresensi.model.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuccessFragment extends Fragment {

    private double latitude;
    private double longitude;
    private final HistoryFragment historyFragment = new HistoryFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_success, container, false);
        // Mengatur visibilitas Bottom Navigation
        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
        // Mendapatkan data dari bundle
        Bundle bundle = getArguments();
        byte[] imageBitmap = bundle.getByteArray("imageBitmap");
        String jenisPresensi = bundle.getString("jenisPresensi");
        String waktuPresensi = bundle.getString("waktuPresensi");
        latitude = bundle.getDouble("latitude", 0.0);
        longitude = bundle.getDouble("longitude", 0.0);
        // Mengonversi imageBitmap menjadi berkas File
        File imageFile = convertByteArrayToFile(imageBitmap);
        // mengambil waktu presensi berupa jam
        String[] parts = waktuPresensi.split(" ");
        String jam = parts[0].trim();
        // Konversi data ke RequestBody
        RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(latitude));
        RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(longitude));
        MultipartBody.Part imagePart = prepareImageFilePart("photo", imageFile); // konversi gambar ke MultipartBody.part
        RequestBody jenisPresensiBody = RequestBody.create(MediaType.parse("text/plain"), jenisPresensi);
        // inisialisasi view
        AppCompatTextView tv_jenisPresensi = rootView.findViewById(R.id.jenisPresensi);
        AppCompatTextView tv_hour = rootView.findViewById(R.id.hour);
        // menyesuaikan jenis presensi dan waktu presensi
        tv_jenisPresensi.setText(jenisPresensi);
        tv_hour.setText(jam);

        // mengirim data ke HistoryFragment
        AppCompatButton buttonCheck = rootView.findViewById(R.id.button_check);
        buttonCheck.setOnClickListener(v -> {
            // mendapatkan token dari sharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            // membuat permintaan untuk mengirim data presensi
            ApiClient.getClient().submitPresence("Bearer " + token, latitudeBody, longitudeBody, imagePart, jenisPresensiBody)
                    .enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            if (response.isSuccessful()) {
                                ServerResponse submitPresenceResponse = response.body();
                                Toast.makeText(getContext(), submitPresenceResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                            Toast.makeText(getContext(), "Failed" + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            // Memulai transaksi fragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, historyFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            // Mengatur item yang dipilih pada Bottom Navigation menjadi item history
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.history);
        });

        return rootView;
    }
    // method untuk mengonversi File menjadi MulitipartBody.Part
    private MultipartBody.Part prepareImageFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
    // Metode untuk mengonversi byte[] menjadi File
    private File convertByteArrayToFile(byte[] byteArray) {
        try {
            File file = File.createTempFile("temp_image", ".jpg");
            Files.write(file.toPath(), byteArray);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}   // end class