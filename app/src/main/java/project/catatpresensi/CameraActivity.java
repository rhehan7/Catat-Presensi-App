package project.catatpresensi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                // Memeriksa izin kamera
                if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Mengambil gambar dari kamera
                    captureImage();
                } else {
                    // Meminta izin kamera
                    requestCameraPermission();
                }
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
                // Izin ditolak, memberi tahu pengguna
                Toast.makeText(this, "izin ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Gambar berhasil diambil dari kamera
            assert data != null;    // data mungkin mengandung null, sehingga diberikan ini
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            // Mendapatkan jenis presensi dari Intent
            Bundle extras = getIntent().getExtras();
            String jenisPresensi = extras.getString("jenisPresensi");
            double latitude = extras.getDouble("latitude");
            double longitude = extras.getDouble("longitude");
            // Mengirimkan hasil gambar, jenisPresensi, kembali ke Fragment Home
            Intent resultIntent = new Intent();
            resultIntent.putExtra("jenisPresensi", jenisPresensi);
            resultIntent.putExtra("latitude", latitude);
            resultIntent.putExtra("longitude", longitude);
            resultIntent.putExtra("data", imageBitmap);
            setResult(RESULT_OK, resultIntent);
            finish();

        } else { onBackPressed(); }
    }


    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(CameraActivity.this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Panggil super.onBackPressed() untuk navigasi ke fragment sebelumnya
    }


}   // end class CameraActivity
