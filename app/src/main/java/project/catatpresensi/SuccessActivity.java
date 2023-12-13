package project.catatpresensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import project.catatpresensi.fragment.HistoryFragment;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // hide action bar
        if (getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }

        //mendapatkan data dari intent
        byte[] imageBitmap = getIntent().getByteArrayExtra("imageBitmap");
        String successText = getIntent().getStringExtra("successText");
        String waktuPresensi = getIntent().getStringExtra("waktuPresensi");

        // mengambil waktu presensi berupa jam
        String[] parts = waktuPresensi.split(" "); // Memisahkan jam, tanggal, bulan, dan tahun
        String jam = parts[0].trim(); // Mengambil jam
        String tanggal = parts[1].trim(); // Mengambil tanggal
        String bulan = parts[2].trim(); // mengambil bulan

        // menampilkan imageView
        ImageView imageView = findViewById(R.id.imageView);
        Bitmap bmp = BitmapFactory.decodeByteArray(imageBitmap, 0, imageBitmap.length);
        imageView.setImageBitmap(bmp);

        // menampilkan teks sukses di activity
        AppCompatTextView jenisPresensi = findViewById(R.id.jenisPresensi);
        AppCompatTextView hour = findViewById(R.id.hour);
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);

        // menyesuaikan jenis presensi dan waktu presensi
        jenisPresensi.setText(successText);
        hour.setText(jam);
        textView.setText(tanggal);
        textView2.setText(bulan);

        // mengirim data ke HistoryFragment
        AppCompatButton buttonCheck = findViewById(R.id.button_check);
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessActivity.this, MainActivity.class);

                // Mengirim data melalui Bundle
                Bundle bundle = new Bundle();
                bundle.putByteArray("imageBitmap", imageBitmap);
                bundle.putString("successText", successText);
                bundle.putString("jam", jam);
                bundle.putString("tanggal", tanggal);
                bundle.putString("bulan", bulan);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}