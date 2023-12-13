package project.catatpresensi.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import project.catatpresensi.R;
import project.catatpresensi.api.ApiClient;
import project.catatpresensi.model.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        // menampilkan data profile
        getDataFromApi(rootView);
        // menemukan view elemen linear layout logout
        LinearLayoutCompat logoutLayout = rootView.findViewById(R.id.logout);
        // set click listener untuk logout
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(); // memanggil fungsi logout
            }
        });

        return rootView; // Inflate the layout for this fragment
    }


    private void getDataFromApi(View rootView) {
        // mendapatkan token dari shared preferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        ApiClient.getClient().getDataProfile("Bearer " + token)
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            ServerResponse profileResponse = response.body();
                            if (profileResponse!= null) {
                                String username = profileResponse.getData().getUsername();
                                String nomorInduk = profileResponse.getData().getNomorInduk();
                                String nomorHandphone = profileResponse.getData().getNomorHandphone();

                                AppCompatTextView tv_nomorInduk = rootView.findViewById(R.id.tv_nomorInduk);
                                AppCompatTextView tv_nomorHp = rootView.findViewById(R.id.tv_nomorHp);
                                AppCompatTextView tv_username = rootView.findViewById(R.id.tv_username);
                                // set Data to TextView
                                tv_nomorInduk.setText(nomorInduk);
                                tv_nomorHp.setText(nomorHandphone);
                                tv_username.setText(username);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {

                    }
                });
    }
    private void logout() {
        // clear token from sharedPreference
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.clear();
        editor.apply();
        setLoginStatus(false); // mengubah status login menjadi false
        requireActivity().finishAndRemoveTask(); // menutup activity saat ini
    }
    private void setLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }


}   // end class ProfileFragment