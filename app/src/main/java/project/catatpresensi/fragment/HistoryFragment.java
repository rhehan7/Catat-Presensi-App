package project.catatpresensi.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import project.catatpresensi.R;
import project.catatpresensi.api.ApiClient;
import project.catatpresensi.data.PresensiAdapter;
import project.catatpresensi.data.PresensiItem;
import project.catatpresensi.model.ServerResponseHistory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private PresensiAdapter presensiAdapter;
    private ArrayList<PresensiItem> presensiList;
    private final String TAG = "HistoryFragment";
    private AppCompatTextView bulanTextView;
    private AppCompatTextView tahunTextView;
    private String selectedBulan;
    private String selectedTahun;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        // Mengambil referensi ke Bottom Navigation dari View Fragment
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);   // Mengatur visibilitas Bottom Navigation
        // Inisialisasi bulan dan tahun saat ini (follow system)
        Calendar calendar = Calendar.getInstance();
        selectedBulan = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)];
        selectedTahun = String.valueOf(calendar.get(Calendar.YEAR));
        // inisialisasi bulan dan tahun pada filter layout
        bulanTextView = rootView.findViewById(R.id.bulan);
        tahunTextView = rootView.findViewById(R.id.tahun);
        bulanTextView.setText(selectedBulan);
        tahunTextView.setText(selectedTahun);
        // menampilkan AlertDialog spinner bulan dan tahun
        LinearLayoutCompat filterLayout = rootView.findViewById(R.id.filterTime_layout);
        filterLayout.setOnClickListener(v -> showFilterDialog());

        setupView(rootView);
        presensiList = new ArrayList<>();
        setupRecyclerView();
        getDataFromApi();

        return rootView;
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Pilih Bulan dan Tahun");
        // Menambahkan spinner untuk pilihan bulan dan tahun di dalam alert dialog
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        Spinner spinnerBulan = view.findViewById(R.id.spinner_bulan);
        Spinner spinnerTahun = view.findViewById(R.id.spinner_tahun);
        // Inisialisasi data untuk Spinner bulan
        String[] arrayBulan = generateArrayBulan();
        ArrayAdapter<String> bulanAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arrayBulan);
        bulanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBulan.setAdapter(bulanAdapter);
        // Inisialisasi data untuk Spinner tahun
        String[] arrayTahun = generateArrayTahun();
        ArrayAdapter<String> tahunAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arrayTahun);
        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTahun.setAdapter(tahunAdapter);

        builder.setView(view);

        builder.setPositiveButton("filter", (dialog, which) -> {
            // Dapatkan nilai yang dipilih dari Spinner
            String selectedBulan = spinnerBulan.getSelectedItem().toString();
            String selectedTahun = spinnerTahun.getSelectedItem().toString();
            // setel nilai variabel global
            this.selectedBulan = selectedBulan;
            this.selectedTahun = selectedTahun;
            // setel teks bulan dan tahun
            bulanTextView.setText(selectedBulan);
            tahunTextView.setText(selectedTahun);
            // Teruskan nilai yang dipilih ke adapter
            String selectedMonthYear = selectedBulan + " " + selectedTahun;
            presensiAdapter.filterDataByMonthYear(selectedMonthYear);
            // Mengammbil data berdasarkan bulan dan tahun yang dipiliih
            getDataFromApi();
        });

        builder.setNegativeButton("Batal", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }
    private String[] generateArrayTahun() {
        int tahunSaatIni = Calendar.getInstance().get(Calendar.YEAR);
        int tahunAwal = 2022;
        int jumlahTahun = tahunSaatIni - tahunAwal + 1;

        String[] arrayTahun = new String[jumlahTahun];
        for (int i = 0; i < jumlahTahun; i++){
            arrayTahun[i] = String.valueOf(tahunAwal + i);
        }
        return arrayTahun;
    }
    private String[] generateArrayBulan() {
        Locale bahasaIndonesia = new Locale("id", "ID");
        DateFormatSymbols symbols = new DateFormatSymbols();
        String[] namaBulan = symbols.getMonths();
        // Hapus nilai yang tidak diperlukan (nama bulan kosong di akhir array)
        ArrayList<String> daftarBulan = new ArrayList<>(Arrays.asList(namaBulan));
        daftarBulan.removeAll(Collections.singleton(""));
        // Ubah ArrayList menjadi array biasa
        return daftarBulan.toArray(new String[0]);
    }
    private void setupRecyclerView() {
        presensiAdapter = new PresensiAdapter(presensiList, new PresensiAdapter.onAdapterListener() {
            @Override
            public void onClick(PresensiItem item) {
//                Toast.makeText(getActivity(), item.getJenisPresensi(), Toast.LENGTH_SHORT).show();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(presensiAdapter);
    }
    private void setupView(View rootview) {
        recyclerView = rootview.findViewById(R.id.recyclerView);
    }
    private void getDataFromApi() {
        // mendapatkan token dari sharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        // Konversi bulan menjadi angka
        int bulanNumber = convertBulanToNumber(this.selectedBulan);
        // Konversi tahun menjadi angka
        int tahunNumber = Integer.parseInt(this.selectedTahun);


        ApiClient.getClient().getPresensiHistory("Bearer " + token, tahunNumber, bulanNumber)
                .enqueue(new Callback<ServerResponseHistory>() {
                    @Override
                    public void onResponse(Call<ServerResponseHistory> call, Response<ServerResponseHistory> response) {
                        Log.d(TAG, response.toString());
                        if (response.isSuccessful()) {
                            ServerResponseHistory serverResponseHistory = response.body();
                            if (serverResponseHistory != null && serverResponseHistory.isSuccess()) {
                                List<PresensiItem> items = serverResponseHistory.getData();
                                Log.d(TAG, items.toString());
                                presensiList.clear();
                                presensiList.addAll(items);
                                presensiAdapter.notifyDataSetChanged();
                            } else {
                                // Handle response jika success == false
                            }
                        } else {
                            // Handle respons jika tidak berhasil (response code bukan 200 OK)
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponseHistory> call, Throwable t) {
                        Log.d(TAG, t.toString());
                        // Handle kegagalan komunikasi dengan server
                    }
                });

    }

    private int convertBulanToNumber(String bulan) {
        DateFormatSymbols symbols = new DateFormatSymbols();
        String[] namaBulan = symbols.getMonths();
        for (int i = 0; i < namaBulan.length; i++) {
            if (namaBulan[i].equalsIgnoreCase(bulan)) {
                // jika nama bulan cocok, kembalikan nomor bulan
                return i + 1; // tambahkan 1 karena indeks bulan dimulai dari 0
            }
        }
        return 1; // nilai default jika tidak ada yang cocok
    }


} // end class HistroryFragment