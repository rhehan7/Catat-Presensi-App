package project.catatpresensi.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.catatpresensi.R;

public class PresensiAdapter extends RecyclerView.Adapter<PresensiAdapter.PresensiViewHolder> {
    private ArrayList<PresensiItem> presensiList;
    private onAdapterListener listener;

    public PresensiAdapter(ArrayList<PresensiItem> presensiList, onAdapterListener listener) {
        this.presensiList = presensiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PresensiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_presensi, parent, false);
        return new PresensiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PresensiViewHolder holder, int position) {
        PresensiItem currentPresensi = presensiList.get(position);

        Picasso.get()
                .load(currentPresensi.getPhoto())
                .fit().centerCrop()
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(currentPresensi);
            }
        });

        holder.jenisPresensi.setText(currentPresensi.getType());
        holder.jam.setText(currentPresensi.getTime());
        holder.tanggal.setText(currentPresensi.getDate_number());
        holder.bulan.setText(currentPresensi.getBulan());
    }

    @Override
    public int getItemCount() {
        return presensiList.size();
    }

    public class PresensiViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView imageView;
        public AppCompatTextView jenisPresensi, jam, tanggal, bulan;

        public PresensiViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_row);
            jenisPresensi = itemView.findViewById(R.id.tv_rowJp);
            jam = itemView.findViewById(R.id.tv_rowWp);
            tanggal = itemView.findViewById(R.id.tv_rowDate);
            bulan = itemView.findViewById(R.id.tv_rowMonth);
        }
    }
    public interface onAdapterListener {
        void onClick(PresensiItem item);
    }

    public void filterDataByMonthYear(String selectedMonthYear) {
        // Buat daftar baru untuk menyimpan data yang sesuai dengan filter
        ArrayList<PresensiItem> filteredList = new ArrayList<>();

        // Loop melalui semua item dalam daftar presensi yang ada
        for (PresensiItem item : presensiList) {
            // Ambil bulan dan tahun dari item saat ini
            String itemMonthYear = item.getBulan() + " " + item.getDate_number();

            // Bandingkan dengan bulan dan tahun yang dipilih
            if (itemMonthYear.equals(selectedMonthYear)) {
                // Jika sesuai, tambahkan item ke daftar yang difilter
                filteredList.add(item);
            }
        }
        // Perbarui data dalam adapter dengan data yang telah difilter
        presensiList = filteredList;
        notifyDataSetChanged();
    }

} // end class PresensiAdapter
