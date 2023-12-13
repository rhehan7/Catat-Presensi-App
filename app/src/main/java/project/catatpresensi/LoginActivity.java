package project.catatpresensi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import project.catatpresensi.api.ApiClient;
import project.catatpresensi.model.LoginRequest;
import project.catatpresensi.model.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private AppCompatEditText prompt_kp, prompt_ni, prompt_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // bind atribute
        AppCompatButton buttonLog = findViewById(R.id.button_log);
        prompt_kp = findViewById(R.id.prompt_kp);
        prompt_ni = findViewById(R.id.prompt_ni);
        prompt_pin = findViewById(R.id.prompt_pin);
        // click button action
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check login with API
                login();
            }
        });
    }

    private void login() {  // berfungsi untuk mengirim permintaan login menggunakan Retrofit dan mengolah respons dari server
        // mengambil data dari input fields
        String kodePerusahaan = prompt_kp.getText().toString();
        String nomorInduk = prompt_ni.getText().toString();
        String pin = prompt_pin.getText().toString();

        // membuat objek LoginResponse
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCompanyCode(kodePerusahaan);
        loginRequest.setIdentity(nomorInduk);
        loginRequest.setPin(pin);

        // membuat permintaan login
        Call<ServerResponse> call = ApiClient.getClient().loginResponse(loginRequest);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    // Berhasil menerima respons dari server
                    ServerResponse serverResponse = response.body();
                    // Mengecek apakah terdapat token dalam respons
                    if (serverResponse != null && serverResponse.getData().getToken() != null) {
                        saveTokenToSharedPreferences(serverResponse.getData().getToken()); // menyimpan token dalam SharedPreferences untuk otorisasi
//                        saveUsernameToSharedPreferences(serverResponse.getData().getUsername()); // menyimpan username dalam SharedPreferences
                        setLoginStatus(true); // mengubah status login menjadi true
                        showToast(serverResponse.getMessage());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class); // berpindah ke halaman MainActivity
                        startActivity(intent);
                    } else {
                        showToast("Login Gagal: " + serverResponse.getMessage());
                    }
                } else {
                    showToast("Login Gagal");
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showToast("Error: " + t.getLocalizedMessage());
            }
        });
    }
//    private void saveUsernameToSharedPreferences(String username) {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("username", username);
//        editor.apply();
//    }
    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
    private void setLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}   // end class LoginActivity