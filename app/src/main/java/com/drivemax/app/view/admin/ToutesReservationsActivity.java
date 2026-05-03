 
package com.drivemax.app.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.drivemax.app.databinding.ActivityToutesReservationsBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Reservation;
import com.drivemax.app.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ToutesReservationsActivity extends AppCompatActivity {

    private ActivityToutesReservationsBinding binding;
    private AppDatabase db;
    private ExecutorService executor;
    private ResAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToutesReservationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(0xFF6C47FF);
        db       = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        binding.btnBack.setOnClickListener(v -> finish());

        binding.fabNouvelleReservation.setOnClickListener(v ->
                startActivity(new Intent(this, NouvelleReservationActivity.class)));

        adapter = new ResAdapter(r -> ouvrirContrat(r));
        binding.recyclerReservations.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerReservations.setAdapter(adapter);

        observeReservations();

        binding.chipAll.setOnClickListener(v -> observeReservations());
        binding.chipEnCours.setOnClickListener(v -> filterByStatut("EN_COURS"));
        binding.chipEnAttente.setOnClickListener(v -> filterByStatut("EN_ATTENTE"));
        binding.chipTerminee.setOnClickListener(v -> filterByStatut("TERMINEE"));
    }

    private void ouvrirContrat(Reservation r) {
        Intent i = new Intent(this, ContratActivity.class);
        i.putExtra("reservation_id", r.getId());
        i.putExtra("voiture_id",     r.getVoitureId());
        i.putExtra("client_id",      r.getClientId());
        startActivity(i);
    }

    private void observeReservations() {
        db.reservationDao().getAllReservations().observe(this, list -> {
            applyReservationList(list);
        });
    }

    private void filterByStatut(String statut) {
        db.reservationDao().getReservationsByStatut(statut).observe(this,
                this::applyReservationList);
    }

    private void applyReservationList(List<Reservation> list) {
        adapter.setData(list);
        int count = list == null ? 0 : list.size();
        binding.tvTotalReservations.setText(formatReservationCount(count));
        binding.tvEmptyState.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
    }

    private String formatReservationCount(int count) {
        if (count <= 1) return count + " reservation";
        return count + " reservations";
    }

    @Override protected void onResume() { super.onResume(); observeReservations(); }
    @Override protected void onDestroy() { super.onDestroy(); executor.shutdown(); }

    // ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ Adapter ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬ÃƒÂ¢Ã¢â‚¬ÂÃ¢â€šÂ¬
    static class ResAdapter extends RecyclerView.Adapter<ResAdapter.VH> {
        interface OnClick { void onClick(Reservation r); }
        private List<Reservation> data = new ArrayList<>();
        private final OnClick listener;
        private final SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yy", Locale.FRANCE);

        ResAdapter(OnClick l) { this.listener = l; }

        void setData(List<Reservation> list) {
            data = list != null ? list : new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            Reservation r = data.get(pos);

            // Charger le nom voiture depuis l'ID (affiche l'ID court en attendant)
            h.tvVoiture.setText("R\u00e9servation #" + r.getId().substring(0, 6).toUpperCase());
            h.tvDateDebut.setText(sdf.format(new Date(r.getDateDebut())));
            h.tvDateFin.setText(sdf.format(new Date(r.getDateFin())));

            long jours = (r.getDateFin() - r.getDateDebut()) / (1000L * 60 * 60 * 24);
            h.tvDuree.setText(jours + " jour(s)");
            h.tvPrixTotal.setText(String.format(Locale.FRANCE, "%.0f MAD", r.getPrixTotal()));
            h.tvStatut.setText(r.getStatut() != null ? r.getStatut() : "-");

            // Couleur statut
            int color;
            switch (r.getStatut() != null ? r.getStatut() : "") {
                case "CONFIRMEE": color = 0xFF10B981; break;
                case "EN_COURS":  color = 0xFF6C47FF; break;
                case "TERMINEE":  color = 0xFF9CA3AF; break;
                default:          color = 0xFFF59E0B; break;
            }
            h.tvStatut.setTextColor(color);

            // Retard
            boolean retard = r.getDateFin() < System.currentTimeMillis()
                    && !"TERMINEE".equals(r.getStatut());
            h.tvRetard.setVisibility(retard ? View.VISIBLE : View.GONE);

            h.itemView.setOnClickListener(v -> listener.onClick(r));
        }

        @Override public int getItemCount() { return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvVoiture, tvDateDebut, tvDateFin, tvDuree, tvStatut, tvPrixTotal, tvRetard;
            VH(View v) {
                super(v);
                tvVoiture   = v.findViewById(R.id.tvReservationId);
                tvDateDebut = v.findViewById(R.id.tvDateDebut);
                tvDateFin   = v.findViewById(R.id.tvDateFin);
                tvDuree     = v.findViewById(R.id.tvDuree);
                tvStatut    = v.findViewById(R.id.tvStatut);
                tvPrixTotal = v.findViewById(R.id.tvPrixTotal);
                tvRetard    = v.findViewById(R.id.tvRetard);
            }
        }
    }
}
