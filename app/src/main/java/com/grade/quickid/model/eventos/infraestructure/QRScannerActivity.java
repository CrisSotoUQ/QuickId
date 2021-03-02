package com.grade.quickid.model.eventos.infraestructure;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.grade.quickid.R;
import com.grade.quickid.model.Estadisticas.aplication.CrearEstadistica;
import com.grade.quickid.model.Estadisticas.domain.Estadistica;
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.registros.aplication.CrearRegistro;
import com.grade.quickid.model.registros.domain.Registro;
import com.grade.quickid.model.Time;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Clase que controla el scanner QR de la aplicación
 *
 * @author Cristian Camilo Soto
 */
public class QRScannerActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private CodeScannerView scannView;
    private TextView resultData;
    private DatabaseReference databaseReference;
    private DatabaseReference myRefRegistroEvento;
    private DatabaseReference myRefEvento;
    private FirebaseDatabase firebaseDatabase;
    private int processDone = 0;
    private ValueEventListener mEventListenerRegistroEvento;
    private ValueEventListener mEventListenerEvento;
    private int contadorMatch =0;
    static boolean isWithin100m;
    private static LatLng latLng ;
    private static FusedLocationProviderClient client ;
    CrearRegistro crearRegistro = new CrearRegistro();
    CrearEstadistica crearEstadistica = new CrearEstadistica();


    /**
     * se cargan todos los componentes
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);
        client = LocationServices.getFusedLocationProviderClient(QRScannerActivity.this);
        scannView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannView);
        resultData = findViewById(R.id.txtResult);
        resultData.setText("");
        obtenerLocation(1);
        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        inicializarFirebase();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String idActividad = result.getText();
                        String idUsuario = user.getUid();
                        // valido el identificador de los codigos QR por ahora un numero
                        // default 23, con el fin de separarlos del resto de QRS en el mundo
                        if (idActividad.substring(0, 2).equals("23")) {
                            mEventListenerEvento = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                            String claveEventoPersona = idActividad + "" + idUsuario;
                                            String idRegistro = UUID.randomUUID().toString();
                                            Evento evento = objSnapshot.getValue(Evento.class);
                                            // valido si tiene activa la opcion de cargue archivo
                                            if (evento.getCargueArchivoStatus().equals("1")) {
                                                // busco los participantes cargados previamente en la lista del evento
                                                for (String value : evento.getListaPersonas().values()) {
                                                    if (user.getEmail().equals(value)) {
                                                        resultData.setText("Encontrado");
                                                        contadorMatch++;
                                                        pause();
                                                        break;
                                                    }
                                                }
                                                if (contadorMatch > 0) {
                                                    snapshotRegistro(objSnapshot, result, claveEventoPersona, idRegistro);
                                                } else {
                                                    resultData.setText("no estas en la lista");
                                                    closeEventListener();
                                                    vibrar();
                                                    break;

                                                }
                                                // si no entonces es una actividad normal voy a registros
                                            } else {
                                                snapshotRegistro(objSnapshot, result, claveEventoPersona, idRegistro);
                                            }

                                        }
                                    } else {
                                        resultData.setText("El evento no existe");
                                        vibrar();
                                        closeEventListener();
                                        reloadActivity();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    System.out.println("Fallo la lectura: " + error.getCode());

                                }
                            };
                            FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
                            myRefEvento = firebaseDatabase2.getInstance().getReference().child("Evento");
                            myRefEvento.orderByChild("idEvento").equalTo(idActividad).addValueEventListener(mEventListenerEvento);
                        } else {
                            resultData.setText("Codigo QR invalido");
                            vibrar();
                            reloadActivity();
                        }

                    }
                });
            }
        });
    }

    private void closeEventListener() {
        myRefEvento.removeEventListener(mEventListenerEvento);
    }

    private void obtenerLocation(int flagLocation) {
        int flag = flagLocation;
        if (ActivityCompat.checkSelfPermission(QRScannerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(QRScannerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        Task<Location> task = client.getLastLocation();

        if (flag != 0) {
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //satisfactorio
                    if (location != null) {
                        //sincronizoLatLng
                        LatLng latLong;
                        latLong = new LatLng(location.getLatitude(), location.getLongitude());
                        latLng = latLong;
                    }
                }
            });
        }
    }

    private void vibrar() {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        vibrator.vibrate(500);
    }

    private void pause() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 500);
    }

    private void reloadActivity() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(getIntent());
            }
        }, 1500);

    }

    /**
     * Funcion que controla las decisiones que se deben tomar para registros
     * segun las opciones de localizacion
     *
     * @param objSnapshot
     * @param result
     * @param claveActPer
     * @param idRegistro
     */
    private void snapshotRegistro(DataSnapshot objSnapshot, Result result, String claveActPer, String idRegistro) {

        calcularDistanciaEntreLocalizacionUsuarioYEvento(objSnapshot);

        mEventListenerRegistroEvento = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && processDone != 1) {
                    processDone = 1;
                    int contadorRegistroFechaActual = 0;
                    int parametroCantidadRegistrosEnUnDia = 0;
                    ArrayList<Registro> listRegistros = new ArrayList<Registro>();
                    for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                        Registro ra = objSnapshot.getValue(Registro.class);
                        listRegistros.add(ra);
                        Time time = new Time();
                        if (ra.getFechaRegistro().equals((time.fecha()))) {
                            contadorRegistroFechaActual++;
                        }
                    }
                    if (contadorRegistroFechaActual > parametroCantidadRegistrosEnUnDia) {
                        resultData.setText("Ya se registro en esta fecha");
                        reloadActivity();
                    } else {
                        myRefRegistroEvento = databaseReference.child("Registro");
                        for (int i = 0; i < listRegistros.size(); i++) {
                            Registro ra = listRegistros.get(i);
                            String key = String.valueOf(ra.getIdRegistro());
                            ra.setVisibilidad("0");
                            myRefRegistroEvento.child(key).setValue(ra);
                        }
                        //creo el nodo Registro evento
                        gestionarRegistro(objSnapshot, result, claveActPer, idRegistro);
                    }
                } else {
                    if (processDone == 0) {
                        processDone++;
                        //creo el nodo Registro evento
                        gestionarRegistro(objSnapshot, result, claveActPer, idRegistro);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        FirebaseDatabase firebaseDatabase3 = FirebaseDatabase.getInstance();
        myRefRegistroEvento = firebaseDatabase3.getInstance().getReference().child("Registro");
        myRefRegistroEvento.orderByChild("idAct_idPer").equalTo(claveActPer).addValueEventListener(mEventListenerRegistroEvento);
        vibrar();
    }

    /**
     * funcion que calcula la distancia entre dos puntos en el espacio
     *
     * @param objSnapshot
     */
    private void calcularDistanciaEntreLocalizacionUsuarioYEvento(DataSnapshot objSnapshot) {
        // valido que la variable global latlong este llena para encontrar la distancia entre dos puntos en el espacio
        if (latLng != null) {
            double latActividad = objSnapshot.child("latitud").getValue(double.class);
            double longActividad = objSnapshot.child("longitud").getValue(double.class);
            float[] results = new float[1];
            Location.distanceBetween(latActividad, longActividad, latLng.latitude, latLng.longitude, results);
            float distanceInMeters = results[0];
            // se establecen 100 metros como distancia maxima, por temas de error en la captura
            isWithin100m = distanceInMeters < 100;
        }
    }
    private void gestionarRegistro(DataSnapshot objSnapshot, Result result, String claveActPer, String idRegistro) {
        String geoLocStatus = objSnapshot.child("geolocStatus").getValue(String.class);
        if (geoLocStatus.equals("1")) {
            if (latLng != null && isWithin100m == false) {
                resultData.setText("Fuera de rango del evento");
                closeEventListeners();
                reloadActivity();
            }
            if (latLng != null && isWithin100m == true) {
                Registro registro = (Registro) crearRegistro.CrearObjetoRegistro(objSnapshot, result, claveActPer, idRegistro);
                final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Registro");
                myRef2.getRef().child(idRegistro).setValue(registro);

                Estadistica estadistica = (Estadistica) crearEstadistica.setearEstadisticas(objSnapshot,result,claveActPer,idRegistro);
                final DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("Estadistica");
                myRef4.getRef().child(estadistica.getIdEvento()).setValue(estadistica);
                resultData.setText("Registro Exitoso");
                closeEventListeners();
                reloadActivity();
            }
        } else {
            Registro registro2 = (Registro) crearRegistro.CrearObjetoRegistro(objSnapshot, result, claveActPer, idRegistro);
            final DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Registro");
            myRef3.getRef().child(idRegistro).setValue(registro2);

            Estadistica estadistica = (Estadistica) crearEstadistica.setearEstadisticas(objSnapshot,result,claveActPer,idRegistro);
            final DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("Estadistica");
            myRef4.getRef().child(estadistica.getIdEvento()).setValue(estadistica);

            resultData.setText("Registro Exitoso");
            closeEventListeners();
            reloadActivity();
        }
    }
    private void closeEventListeners() {
        myRefRegistroEvento.removeEventListener(mEventListenerRegistroEvento);
        myRefEvento.removeEventListener(mEventListenerEvento);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCodeScanner.releaseResources();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(QRScannerActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }


}
