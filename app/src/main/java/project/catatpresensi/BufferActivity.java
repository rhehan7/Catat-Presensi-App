package project.catatpresensi;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BufferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer);
        //Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // menampilkan dialog peringatan untuk menutup aplikasi jika terdeteksi lokasi palsu
        showFakeLocationDialog();
    }

    private void showFakeLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan")
                .setMessage("Anda menggunakan lokasi palsu. Keluar dari aplikasi!")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // Keluar dari aplikasi
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}   // end of class BufferActivity