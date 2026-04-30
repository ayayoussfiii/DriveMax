@"
package com.drivemax.app.view.admin;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.drivemax.app.databinding.ActivityContratBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Client;
import com.drivemax.app.model.entities.Reservation;
import com.drivemax.app.model.entities.Voiture;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContratActivity extends AppCompatActivity {

    private ActivityContratBinding binding;
    private AppDatabase db;
    private ExecutorService executor;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
    private Reservation reservation;
    private Voiture voiture;
    private Client client;
    private String numContrat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContratBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();

        String reservationId = getIntent().getStringExtra("reservation_id");
        String voitureId = getIntent().getStringExtra("voiture_id");
        String clientId = getIntent().getStringExtra("client_id");

        loadContrat(reservationId, voitureId, clientId);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnRetour.setOnClickListener(v -> finish());
        binding.btnConfirmer.setOnClickListener(v -> confirmerContrat());
        binding.btnPrint.setOnClickListener(v -> genererEtOuvrirPDF());
        binding.btnPartager.setOnClickListener(v -> partagerPDF());
    }

    private void loadContrat(String reservationId, String voitureId, String clientId) {
        executor.execute(() -> {
            reservation = db.reservationDao().getReservationById(reservationId);
            voiture = db.voitureDao().getVoitureById(voitureId);
            client = db.clientDao().getClientById(clientId);
            runOnUiThread(() -> {
                if (reservation == null || voiture == null || client == null) return;

                numContrat = "DM-" +
                    new SimpleDateFormat("yyyy", Locale.FRANCE).format(new Date()) +
                    "-" + reservationId.substring(0, 6).toUpperCase();

                binding.tvNumeroContrat.setText("N " + numContrat);
                binding.tvClientNom.setText(client.getNomComplet());
                binding.tvClientCin.setText(client.getCin() != null ? client.getCin() : "-");
                binding.tvClientTel.setText(client.getTelephone() != null ? client.getTelephone() : "-");
                binding.tvClientEmail.setText(client.getEmail() != null ? client.getEmail() : "-");
                binding.tvVoitureNom.setText(voiture.getMarque() + " " + voiture.getModele());
                binding.tvVoitureImmat.setText(voiture.getImmatriculation() != null ? voiture.getImmatriculation() : "-");
                binding.tvVoitureAnnee.setText(String.valueOf(voiture.getAnnee()));
                binding.tvDateDebut.setText(sdf.format(new Date(reservation.getDateDebut())));
                binding.tvDateFin.setText(sdf.format(new Date(reservation.getDateFin())));
                binding.tvNombreJours.setText(reservation.getNombreJours() + " jours");
                binding.tvPrixJour.setText(String.format("%.0f MAD/jour", voiture.getPrixJournalier()));
                binding.tvPrixTotal.setText(String.format("%.0f MAD", reservation.getPrixTotal()));
                binding.tvStatut.setText(reservation.getStatut());
                binding.tvDateContrat.setText("Fait le " + sdf.format(new Date()));
            });
        });
    }

    private void confirmerContrat() {
        executor.execute(() -> {
            db.reservationDao().updateStatut(reservation.getId(), "CONFIRMEE");
            runOnUiThread(() -> {
                binding.tvStatut.setText("CONFIRMEE");
                Toast.makeText(this, "Contrat confirme", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private File genererPDF() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paintBlue = new Paint();
        paintBlue.setColor(Color.parseColor("#4361EE"));
        paintBlue.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, 595, 100, paintBlue);

        Paint paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setTextSize(28);
        paintWhite.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DRIVEMAX", 40, 50, paintWhite);

        Paint paintWhiteSm = new Paint();
        paintWhiteSm.setColor(Color.WHITE);
        paintWhiteSm.setTextSize(13);
        canvas.drawText("Contrat de Location de Vehicule", 40, 75, paintWhiteSm);

        Paint paintNumero = new Paint();
        paintNumero.setColor(Color.WHITE);
        paintNumero.setTextSize(13);
        paintNumero.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("N " + numContrat, 555, 60, paintNumero);

        Paint paintTitle = new Paint();
        paintTitle.setColor(Color.parseColor("#1A1A2E"));
        paintTitle.setTextSize(16);
        paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint paintText = new Paint();
        paintText.setColor(Color.parseColor("#424242"));
        paintText.setTextSize(13);

        Paint paintLine = new Paint();
        paintLine.setColor(Color.parseColor("#E0E0E0"));
        paintLine.setStrokeWidth(1);

        int y = 130;

        canvas.drawText("INFORMATIONS CLIENT", 40, y, paintTitle);
        y += 8;
        canvas.drawLine(40, y, 555, y, paintLine);
        y += 20;

        canvas.drawText("Nom complet : " + (client != null ? client.getNomComplet() : "-"), 40, y, paintText); y += 22;
        canvas.drawText("CIN : " + (client != null && client.getCin() != null ? client.getCin() : "-"), 40, y, paintText); y += 22;
        canvas.drawText("Telephone : " + (client != null && client.getTelephone() != null ? client.getTelephone() : "-"), 40, y, paintText); y += 22;
        canvas.drawText("Email : " + (client != null && client.getEmail() != null ? client.getEmail() : "-"), 40, y, paintText); y += 35;

        canvas.drawText("INFORMATIONS VEHICULE", 40, y, paintTitle);
        y += 8;
        canvas.drawLine(40, y, 555, y, paintLine);
        y += 20;

        canvas.drawText("Vehicule : " + (voiture != null ? voiture.getMarque() + " " + voiture.getModele() : "-"), 40, y, paintText); y += 22;
        canvas.drawText("Immatriculation : " + (voiture != null && voiture.getImmatriculation() != null ? voiture.getImmatriculation() : "-"), 40, y, paintText); y += 22;
        canvas.drawText("Annee : " + (voiture != null ? voiture.getAnnee() : "-"), 40, y, paintText); y += 22;
        canvas.drawText("Carburant : " + (voiture != null && voiture.getCarburant() != null ? voiture.getCarburant() : "-"), 40, y, paintText); y += 35;

        canvas.drawText("DETAILS LOCATION", 40, y, paintTitle);
        y += 8;
        canvas.drawLine(40, y, 555, y, paintLine);
        y += 20;

        canvas.drawText("Date debut : " + (reservation != null ? sdf.format(new Date(reservation.getDateDebut())) : "-"), 40, y, paintText); y += 22;
        canvas.drawText("Date fin : " + (reservation != null ? sdf.format(new Date(reservation.getDateFin())) : "-"), 40, y, paintText); y += 22;
        canvas.drawText("Duree : " + (reservation != null ? reservation.getNombreJours() + " jours" : "-"), 40, y, paintText); y += 22;
        canvas.drawText("Prix/jour : " + (voiture != null ? String.format("%.0f MAD", voiture.getPrixJournalier()) : "-"), 40, y, paintText); y += 22;

        Paint paintPrixTotal = new Paint();
        paintPrixTotal.setColor(Color.parseColor("#4361EE"));
        paintPrixTotal.setTextSize(18);
        paintPrixTotal.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("PRIX TOTAL : " + (reservation != null ? String.format("%.0f MAD", reservation.getPrixTotal()) : "-"), 40, y + 10, paintPrixTotal);
        y += 50;

        canvas.drawText("CONDITIONS GENERALES", 40, y, paintTitle);
        y += 8;
        canvas.drawLine(40, y, 555, y, paintLine);
        y += 20;

        Paint paintCond = new Paint();
        paintCond.setColor(Color.parseColor("#757575"));
        paintCond.setTextSize(11);
        canvas.drawText("1. Le locataire s'engage a restituer le vehicule dans l'etat dans lequel il l'a recu.", 40, y, paintCond); y += 18;
        canvas.drawText("2. Tout dommage cause au vehicule sera a la charge du locataire.", 40, y, paintCond); y += 18;
        canvas.drawText("3. Le vehicule doit etre utilise conformement au code de la route.", 40, y, paintCond); y += 18;
        canvas.drawText("4. Le carburant est a la charge du locataire.", 40, y, paintCond); y += 18;
        canvas.drawText("5. En cas de retard, une penalite de 200 MAD/jour sera appliquee.", 40, y, paintCond); y += 40;

        canvas.drawLine(40, y, 260, y, paintLine);
        canvas.drawLine(335, y, 555, y, paintLine);
        y += 15;
        canvas.drawText("Signature Client", 40, y, paintCond);
        canvas.drawText("Signature DriveMax", 335, y, paintCond);

        Paint paintFooter = new Paint();
        paintFooter.setColor(Color.parseColor("#9E9E9E"));
        paintFooter.setTextSize(10);
        paintFooter.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("DriveMax - Location de Vehicules - " + sdf.format(new Date()), 297, 820, paintFooter);

        document.finishPage(page);

        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(dir, "Contrat_" + numContrat + ".pdf");
        try {
            document.writeTo(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.close();
        return file;
    }

    private void genererEtOuvrirPDF() {
        executor.execute(() -> {
            File pdf = genererPDF();
            runOnUiThread(() -> {
                try {
                    Uri uri = FileProvider.getUriForFile(this,
                            getPackageName() + ".provider", pdf);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Ouvrir avec"));
                } catch (Exception e) {
                    Toast.makeText(this, "Aucun lecteur PDF installe", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void partagerPDF() {
        executor.execute(() -> {
            File pdf = genererPDF();
            runOnUiThread(() -> {
                try {
                    Uri uri = FileProvider.getUriForFile(this,
                            getPackageName() + ".provider", pdf);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/pdf");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Contrat DriveMax " + numContrat);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Partager via"));
                } catch (Exception e) {
                    Toast.makeText(this, "Erreur partage", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
"@ | Out-File -FilePath "C:\Users\lenovo\Downloads\DriveMax\app\src\main\java\com\drivemax\app\view\admin\ContratActivity.java" -Encoding ASCII
