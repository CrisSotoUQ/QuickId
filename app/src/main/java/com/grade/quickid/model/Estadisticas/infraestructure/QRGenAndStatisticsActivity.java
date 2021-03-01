package com.grade.quickid.model.Estadisticas.infraestructure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grade.quickid.BuildConfig;
import com.grade.quickid.R;
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.registros.domain.Registro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenAndStatisticsActivity extends AppCompatActivity {
    private EditText txt_textoqr;
    private TextView textViewNombreActividad;
    private TextView contadorRegistrosFechaActual;
    private TextView contadorRegistrosHistorico;
    private TextView contadorInasistentes;
    private ImageView imgQR;
    private Button btnSaveQr;
    private DatabaseReference myRefestadisticaRegistros;
    final int REQUEST_CODE = 1;
    private String idEvento;
    private ValueEventListener mEventListnerActividad;
    private DatabaseReference myRefActividad;
    private ValueEventListener mEventListEstadisticasRegistros;

    private int v_RegistrosFechaActual = 0;
    private int v_registrosHistorico = 0;
    private int contadorAsistentes = 0;
    private String nombreActividad;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generator);
        imgQR = findViewById(R.id.imageView_qr);
        textViewNombreActividad = (TextView) findViewById(R.id.textViewNombreActividad);
        contadorRegistrosFechaActual = findViewById(R.id.contadorRegistrosFechaActual);
        contadorInasistentes = findViewById(R.id.contadorInasistentesFechaActual);
        contadorRegistrosHistorico = findViewById(R.id.contadorRegistrosHistorico);
        btnSaveQr = (Button) findViewById(R.id.btn_save_qr);

        Intent intent = getIntent();
        idEvento = intent.getStringExtra("idEvento");

        createChart();
        createBarchart();
        nombreActividad = intent.getStringExtra("nombre");
        textViewNombreActividad.setText(nombreActividad);
        titulo();

        // genera el Qrcode
        generateQR(idEvento);

        btnSaveQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(QRGenAndStatisticsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        saveToGallery();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    pedirPermisos();
                }

            }
        });
    }

    private void createChart() {
        ValueEventListener valueEventListenerActividad = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objSnapshot1 : snapshot.getChildren()) {
                    Evento act = objSnapshot1.getValue(Evento.class);

                    ValueEventListener valueEventListenerRegistroActividad = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot objSnapshot2 : snapshot.getChildren()) {
                                    Registro RAct = objSnapshot2.getValue(Registro.class);
                                    v_registrosHistorico++;
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    String date = simpleDateFormat.format(calendar.getTime());
                                    String fecha = objSnapshot2.child("fechaRegistro").getValue(String.class);
                                    if (date.equals(fecha)) {
                                        v_RegistrosFechaActual++;
                                    }
                                }
                                contadorRegistrosHistorico.setText(String.valueOf(v_registrosHistorico));
                                contadorRegistrosFechaActual.setText(String.valueOf(v_RegistrosFechaActual));
                                // obtengo la cantidad de inasistentes
                                contadorAsistentes = act.getListaPersonas().size() - v_RegistrosFechaActual;
                                if (!act.getListaPersonas().isEmpty()){
                                    contadorInasistentes.setText(String.valueOf(contadorAsistentes));
                                }

                            }

                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Fallo la lectura: " + error.getCode());
                        }
                    };
                    FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
                    myRefestadisticaRegistros = firebaseDatabase2.getInstance().getReference().child("Registro");
                    myRefestadisticaRegistros.orderByChild("idEvento").equalTo(act.getIdEvento()).addValueEventListener(valueEventListenerRegistroActividad);
                    mEventListEstadisticasRegistros = valueEventListenerRegistroActividad;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        myRefActividad = firebaseDatabase1.getInstance().getReference().child("Evento");
        myRefActividad.orderByChild("idEvento").equalTo(idEvento).addValueEventListener(valueEventListenerActividad);
        mEventListnerActividad = valueEventListenerActividad;
    }
    private void createBarchart() {
        BarChart barChart = findViewById(R.id.barChart);
        ArrayList<BarEntry> visitorsByDate = new ArrayList<>();
        visitorsByDate.add(new BarEntry(2014, 300));
        visitorsByDate.add(new BarEntry(2015, 500));
        visitorsByDate.add(new BarEntry(2016, 600));
        visitorsByDate.add(new BarEntry(2017, 620));
        visitorsByDate.add(new BarEntry(2018, 500));
        visitorsByDate.add(new BarEntry(2019, 300));
        visitorsByDate.add(new BarEntry(2020, 200));
        visitorsByDate.add(new BarEntry(2021, 600));
        visitorsByDate.add(new BarEntry(2022, 700));
        BarDataSet barDataSet = new BarDataSet(visitorsByDate, "Visitors By Date ");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Bar Chart Example");
        barChart.animateY(2000);
        PieChart pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> visitantes = new ArrayList<>();
        visitantes.add(new PieEntry(508, "2019"));
        visitantes.add(new PieEntry(700, "2020"));
        visitantes.add(new PieEntry(800, "2021"));
        PieDataSet pieDataSet = new PieDataSet(visitantes, "Visitantes");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Visitantes");
        pieChart.animate();

    }

    private void pedirPermisos() {
        ActivityCompat.requestPermissions(QRGenAndStatisticsActivity.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    saveToGallery();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "porfavor otorgar permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToGallery() throws IOException {
        Drawable drawable = imgQR.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        try {
            File file = new File(getApplicationContext().getFilesDir(), File.separator + "Evento.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(Intent.EXTRA_STREAM, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setType("image/jpg");
            startActivity(Intent.createChooser(intent, "Share image via "));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void titulo() {
        this.setTitle("Generador QR");
    }

    private void generateQR(String actividad) {
        QRGEncoder qrgEncoder = new QRGEncoder(actividad, null, QRGContents.Type.TEXT, 2000);
        try {
            Bitmap qrBits = qrgEncoder.getBitmap();
            imgQR.setImageBitmap(qrBits);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myRefestadisticaRegistros.removeEventListener(mEventListEstadisticasRegistros);
        this.finish();

    }
}