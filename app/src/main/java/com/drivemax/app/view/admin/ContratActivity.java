package com.drivemax.app.view.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.drivemax.app.R;
import com.drivemax.app.databinding.ActivityContratsGeneresBinding;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContratsGeneresActivity extends AppCompatActivity {

    private ActivityContratsGeneresBinding binding;
    private ContratFileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContratsGeneresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(0xFF6C47FF);

        adapter = new ContratFileAdapter(this::openPdf);
        binding.recyclerContrats.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerContrats.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> finish());
        loadContracts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContracts();
    }

    private void loadContracts() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        List<File> files = new ArrayList<>();
        if (dir != null && dir.exists()) {
            File[] pdfFiles = dir.listFiles((d, name) -> name != null
                    && name.startsWith("Contrat_")
                    && name.endsWith(".pdf"));
            if (pdfFiles != null) {
                Arrays.sort(pdfFiles, Comparator.comparingLong(File::lastModified).reversed());
                files.addAll(Arrays.asList(pdfFiles));
            }
        }

        adapter.setData(files);
        binding.tvCount.setText(files.size() + (files.size() > 1 ? " contrats" : " contrat"));
        binding.tvEmpty.setVisibility(files.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void openPdf(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Ouvrir le contrat"));
        } catch (Exception e) {
            Toast.makeText(this, "Aucun lecteur PDF installe", Toast.LENGTH_SHORT).show();
        }
    }

    static class ContratFileAdapter extends RecyclerView.Adapter<ContratViewHolder> {
        interface OnClickFile { void onClick(File file); }

        private final List<File> data = new ArrayList<>();
        private final OnClickFile listener;
        private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);

        ContratFileAdapter(OnClickFile listener) {
            this.listener = listener;
        }

        void setData(List<File> files) {
            data.clear();
            if (files != null) data.addAll(files);
            notifyDataSetChanged();
        }

        @Override
        public ContratViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contrat_genere, parent, false);
            return new ContratViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContratViewHolder holder, int position) {
            File file = data.get(position);
            String name = file.getName().replace("Contrat_", "").replace(".pdf", "");
            holder.tvTitle.setText("Contrat " + name);
            holder.tvDate.setText("Genere le " + sdf.format(new Date(file.lastModified())));
            holder.itemView.setOnClickListener(v -> listener.onClick(file));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class ContratViewHolder extends RecyclerView.ViewHolder {
        android.widget.TextView tvTitle;
        android.widget.TextView tvDate;

        ContratViewHolder(android.view.View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvContratTitle);
            tvDate = itemView.findViewById(R.id.tvContratDate);
        }
    }
}
