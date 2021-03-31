package com.grade.quickid.model.estadisticas.infraestructure;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenAndStatisticsActivity extends AppCompatActivity {
    private TextView contadorRegistrosFechaActual;
    private TextView contadorRegistrosHistorico;
    private TextView contadorInasistentes;
    private ImageView imgQR;
    private Button btnSaveQr;
    private DatabaseReference myRefestadisticaRegistros;
    final int REQUEST_CODE = 1;
    private String idEvento;
    private ValueEventListener valueEventListenerEvento;
    private DatabaseReference myRefEvento;
    private ValueEventListener valueEventListenerRegistroEvento;
    private int v_RegistrosFechaActual = 0;
    private int v_registrosHistorico = 0;
    private int contadorAsistentes = 0;
    private String nombreActividad;
    PieChart pieChart;
    BarChart barChart;
    // voy a guardar los años en este array
    final HashMap<String, Integer> arrayMes = new HashMap<String, Integer>();
    HashMap<String, Integer> arrayAnio = new HashMap<String, Integer>();
    final ArrayList<BarEntry> visitorsByDate = new ArrayList<>();
    final ArrayList<PieEntry> visitantes = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generator);
        imgQR = findViewById(R.id.imageView_qr);
        contadorRegistrosFechaActual = findViewById(R.id.contadorRegistrosFechaActual);
        contadorInasistentes = findViewById(R.id.contadorInasistentesFechaActual);
        contadorRegistrosHistorico = findViewById(R.id.contadorRegistrosHistorico);
        btnSaveQr = (Button) findViewById(R.id.btn_save_qr);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        Intent intent = getIntent();
        idEvento = intent.getStringExtra("idEvento");

        createChart();
        nombreActividad = intent.getStringExtra("nombre");
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
        valueEventListenerEvento = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objSnapshot1 : snapshot.getChildren()) {
                    Evento act = objSnapshot1.getValue(Evento.class);

                    valueEventListenerRegistroEvento = new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                v_registrosHistorico = 0;
                                v_RegistrosFechaActual = 0;
                                arrayMes.clear();
                                arrayAnio.clear();
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
                                    StringTokenizer st = new StringTokenizer(RAct.getFechaRegistro(), "-"); //delimitador -
                                    String dia = st.nextToken();
                                    String mes = st.nextToken();
                                    String anio = st.nextToken();
                                    String anioActual = Integer.toString(calendar.get(Calendar.YEAR));
                                    //Estadistica para el mes
                                    if (anioActual.equals(anio)) {
                                        if (arrayMes.containsKey(mes)) {
                                            arrayMes.put(mes, arrayMes.get(mes) + 1);
                                        } else {
                                            arrayMes.put(mes, 1);
                                        }
                                    }
                                    if (arrayAnio.containsKey(anio)) {
                                        arrayAnio.put(anio, arrayAnio.get(anio) + 1);
                                    } else {
                                        arrayAnio.put(anio, 1);
                                    }
                                }
                                contadorRegistrosHistorico.setText(String.valueOf(v_registrosHistorico));
                                contadorRegistrosFechaActual.setText(String.valueOf(v_RegistrosFechaActual));
                                // obtengo la cantidad de inasistentes

                                if (act.getCargueArchivoStatus().equals("1")) {
                                    contadorAsistentes = act.getListaPersonas().size() - v_RegistrosFechaActual;
                                    if (!act.getListaPersonas().isEmpty()) {
                                        contadorInasistentes.setText(String.valueOf(contadorAsistentes));
                                    }
                                }
                            }
                            createBarchart();
                            pieChart();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Fallo la lectura: " + error.getCode());
                        }
                    };
                    FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
                    myRefestadisticaRegistros = firebaseDatabase2.getInstance().getReference().child("Registro");
                    myRefestadisticaRegistros.orderByChild("idEvento").equalTo(act.getIdEvento()).addValueEventListener(valueEventListenerRegistroEvento);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        myRefEvento = firebaseDatabase1.getInstance().getReference().child("Evento");
        myRefEvento.orderByChild("idEvento").equalTo(idEvento).addValueEventListener(valueEventListenerEvento);

    }

    private void createBarchart() {
        visitorsByDate.clear();
        if (arrayMes.size() == 0) {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            visitorsByDate.add(new BarEntry(currentMonth, 0));
        } else {
            for (Map.Entry<String, Integer> entry : arrayMes.entrySet()) {
                String key = entry.getKey();
                Integer contador = entry.getValue();
                visitorsByDate.add(new BarEntry(Integer.parseInt(key), contador));
            }
        }

        BarDataSet barDataSet = new BarDataSet(visitorsByDate, "Asistentes por mes");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10f);
        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("QuickId");
        barChart.animateY(2000);
    }

    private void pieChart() {
        visitantes.clear();
        if (arrayAnio.size() == 0) {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int currentYear = calendar.get(Calendar.YEAR);
            visitantes.add(new PieEntry(currentYear, "0"));
        } else {
            for (Map.Entry<String, Integer> entry : arrayAnio.entrySet()) {
                String key = entry.getKey();
                Integer contador = entry.getValue();
                visitantes.add(new PieEntry(Integer.parseInt(key), String.valueOf(contador)));
            }
        }
        PieDataSet pieDataSet = new PieDataSet(visitantes, "Asistentes por año");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(10f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setText("Quickid");
        pieChart.setCenterText("Asistentes");
        pieChart.invalidate();
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
            Bitmap dest = Bitmap.createBitmap(qrBits.getWidth(), qrBits.getHeight(), Bitmap.Config.ARGB_8888);

            String yourText = nombreActividad;

            Canvas cs = new Canvas(dest);
            Paint tPaint = new Paint();
            tPaint.setTextSize(120);
            tPaint.setColor(Color.BLACK);
            tPaint.setStyle(Paint.Style.FILL);
            cs.drawBitmap(qrBits, 0f, 0f, null);
            float height = tPaint.measureText("yY");
            float width = tPaint.measureText(yourText);
            float x_coord = (qrBits.getWidth() - width)/2;
            cs.drawText(yourText, x_coord, height+20f, tPaint); // 15f is to put space between top edge and the text, if you want to change it, you can
            try {
                dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/ImageAfterAddingText.jpg")));
                // dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
                imgQR.setImageBitmap(dest);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myRefestadisticaRegistros.removeEventListener(valueEventListenerRegistroEvento);
        myRefEvento.removeEventListener(valueEventListenerEvento);
        this.finish();

    }
}