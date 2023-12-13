package project.catatpresensi.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import project.catatpresensi.R;

public class CameraFragment extends Fragment {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private Bitmap capturedImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        // Mengatur visibilitas Bottom Navigation
//        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        return rootView;
    }

    // Pada metode onViewCreated di dalam CameraFragment
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pemeriksaan izin untuk kamera
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Mengambil gambar dari kamera
            captureImage();
        } else {
            // Meminta izin kamera
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{
                Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, ambil gambar dari kamera
                captureImage();
            } else {
                // Meminta izin kamera
                requestCameraPermission();
                // Izin ditolak, beri tahu pengguna
                Toast.makeText(requireContext(), "Izin kamera diperlukan untuk mengambil gambar.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Gambar berhasil diambil dari kamera
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data"); // "data" adalah key default dari sistem
            capturedImage = imageBitmap; // Menyimpan gambar di capturedImage

            // Mendapatkan jenis presensi dari Fragment yang memanggil CameraFragment
            Bundle extras = getArguments();
            String jenisPresensi = extras.getString("jenisPresensi");
            double latitude = extras.getDouble("latitude");
            double longitude = extras.getDouble("longitude");

            showSuccessScreen(jenisPresensi, latitude, longitude);

            getParentFragmentManager().popBackStack();

            // Mengirimkan hasil gambar, jenisPresensi, kembali ke Fragment Home
            // Mengirimkan hasil gambar, jenisPresensi, dll. menggunakan FragmentResult
//            Bundle result = new Bundle();
//            result.putString("jenisPresensi", jenisPresensi);
//            result.putDouble("latitude", latitude);
//            result.putDouble("longitude", longitude);
//            result.putParcelable("data", imageBitmap);

//          getParentFragmentManager().setFragmentResult("cameraResult", result);
            // Membuat instance dari HistoryFragment dan mengirim bundle
//            SuccessFragment successFragment = new SuccessFragment();
//            successFragment.setArguments(result);
//
//            // Memulai transaksi fragment
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.replace(R.id.container, successFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
        }
    }

    private void showSuccessScreen(String jenisPresensi, double latitude, double longitude) {
        // Convert capturedImage to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String waktuPresensi = getTime();
//        String successText = jenisPresensi;
//        double location_lat = latitude;
//        double location_long = longitude;

        // Menggunakan Bundle untuk mengirim data ke SuccessFragment
        Bundle bundle = new Bundle();
        bundle.putByteArray("imageBitmap", byteArray);
        bundle.putString("waktuPresensi", waktuPresensi);
        bundle.putString("jenisPresensi", jenisPresensi);
        bundle.putDouble("latitude", latitude); // Tambahkan latitude
        bundle.putDouble("longitude", longitude); // Tambahkan longitude

        // Membuat instance dari SuccessFragment dan mengirim bundle
        SuccessFragment successFragment = new SuccessFragment();
        successFragment.setArguments(bundle);

        // Memulai transaksi fragment
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Izin diberikan, ambil gambar dari kamera
//                captureImage();
//            } else {
//                // Meminta izin kamera
//                requestCameraPermission();
//                // Izin ditolak, memberi tahu pengguna
//                Toast.makeText(requireContext(), "Izin ditolak", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void requestCameraPermission() {
//        ActivityCompat.requestPermissions(requireActivity(),
//                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//    }
//
//    private void captureImage() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA_REQUEST_CODE);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == FragmentActivity.RESULT_OK) {
//            // Gambar berhasil diambil dari kamera
//            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//
//            // Mendapatkan jenis presensi dari Fragment yang memanggil CameraFragment
//            Bundle extras = getArguments();
//            String jenisPresensi = extras.getString("jenisPresensi");
//            double latitude = extras.getDouble("latitude");
//            double longitude = extras.getDouble("longitude");
//
//            // Mengirimkan hasil gambar, jenisPresensi, kembali ke Fragment Home
//            // Mengirimkan hasil gambar, jenisPresensi, dll. menggunakan FragmentResult
//            Bundle result = new Bundle();
//            result.putString("jenisPresensi", jenisPresensi);
//            result.putDouble("latitude", latitude);
//            result.putDouble("longitude", longitude);
//            result.putParcelable("data", imageBitmap);
//
//            getParentFragmentManager().setFragmentResult("cameraResult", result);
//            getParentFragmentManager().popBackStack(); // Kembali ke fragment sebelumnya
//        }
//    }
}
