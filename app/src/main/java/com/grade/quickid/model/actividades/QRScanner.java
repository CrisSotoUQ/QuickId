package com.grade.quickid.model.actividades;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.registroActividad.RegistroActividad;
import com.grade.quickid.model.Time;

import java.util.ArrayList;
import java.util.UUID;

public class QRScanner extends AppCompatActivity {
    CodeScanner mCodeScanner;
    CodeScannerView scannView;
    TextView resultData;
    static int OnScannerElse;
    FusedLocationProviderClient client;
    DatabaseReference databaseReference;
    DatabaseReference myRefRegistroEvento;
    DatabaseReference myRefEvento;
    FirebaseDatabase firebaseDatabase;
    int processDone = 0;
    ValueEventListener mSendEventListner;
    ValueEventListener mSendEventListner2;
    public static  LatLng latLng;

    public static LatLng getLatLng() {
        return latLng;
    }

    public static void setLatLng(LatLng latLng) {
        QRScanner.latLng = latLng;
    }

    int contadorMatch;
    boolean isWithin10km;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);
        OnScannerElse = 0;
        scannView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannView);
        resultData = findViewById(R.id.txtResult);
        resultData.setText("");
        client = LocationServices.getFusedLocationProviderClient(this);
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
                        FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference2 = firebaseDatabase2.getReference();
                        String idActividad = result.getText();
                        String idUsuario = user.getUid();
                        // valido el identificador de los codigos QR por ahora un numero por default para separarlos del resto de QRS
                        if (idActividad.substring(0, 2).equals("23")) {
                            myRefEvento = firebaseDatabase2.getInstance().getReference().child("Actividad");
                            ValueEventListener valueEventListener2 = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                            String claveActPer = idActividad + "" + idUsuario;
                                            String idRegistro = UUID.randomUUID().toString();
                                            Actividad act = objSnapshot.getValue(Actividad.class);
                                            String cargaStatus = objSnapshot.child("cargueArchivoStatus").getValue(String.class);
                                            String geoStatus = objSnapshot.child("geolocStatus").getValue(String.class);
                                            if (geoStatus.equals("1")) {
                                                getCurrentLocation(1);

                                            }
                                            if (cargaStatus.equals("1")) {
                                                for (String value : act.getListaPersonas().values()) {
                                                    if (user.getEmail().equals(value)) {
                                                        Toast.makeText(QRScanner.this, "Se ha encontrado", Toast.LENGTH_SHORT);
                                                        resultData.setText("Encontrado");
                                                        contadorMatch++;
                                                        break;
                                                    }
                                                }
                                                if (contadorMatch > 0) {
                                                    siguienteSnapshot(objSnapshot, result, claveActPer, idRegistro);
                                                } else {

                                                    resultData.setText("no estas en la lista");
                                                    //vibra el cel
                                                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                                    vibrator.vibrate(500);
                                                    vibrator.vibrate(500);
                                                    reloadActivity();
                                                    break;

                                                }
                                            } else {
                                                siguienteSnapshot(objSnapshot, result, claveActPer, idRegistro);
                                            }

                                        }
                                    } else {
                                        resultData.setText("El evento no existe");
                                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(500);
                                        vibrator.vibrate(500);
                                        reloadActivity();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    System.out.println("Fallo la lectura: " + error.getCode());

                                }
                            };
                            myRefEvento.orderByChild("idActividad").equalTo(idActividad).addValueEventListener(valueEventListener2);
                            mSendEventListner2 = valueEventListener2;
                        } else {
                            resultData.setText("Codigo QR invalido");
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            vibrator.vibrate(500);
                            reloadActivity();
                        }

                    }
                });
            }
        });
    }

    private void getCurrentLocation(int flagLocation) {
        //Initialize task location
        //  Task<>
        final int flaglocation = flagLocation;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(QRScanner.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        Task<Location> task = client.getLastLocation();

        if (flagLocation != 0) {
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //satisfactorio
                    if (location != null) {
                        //sincronizoLatLng
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        QRScanner.setLatLng(latLng);

                    }
                }
            });
        }
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

    private void siguienteSnapshot(DataSnapshot objSnapshot, Result result, String claveActPer, String idRegistro) {
        FirebaseDatabase firebaseDatabase3 = FirebaseDatabase.getInstance();
        myRefRegistroEvento = firebaseDatabase3.getInstance().getReference().child("RegistroActividad");
        if (latLng != null) {
            double latActividad = objSnapshot.child("latitud").getValue(double.class);
            double longActividad = objSnapshot.child("longitud").getValue(double.class);
            float[] results = new float[1];
            Location.distanceBetween(latActividad, longActividad, latLng.latitude, latLng.longitude, results);
            float distanceInMeters = results[0];
            isWithin10km = distanceInMeters < 50;
        }

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && processDone != 1) {
                    processDone = 1;
                    int contador = 0;
                    int parametro = 0;
                    ArrayList<RegistroActividad> list = new ArrayList<RegistroActividad>();
                    for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                        RegistroActividad ra = objSnapshot.getValue(RegistroActividad.class);
                        list.add(ra);
                        Time time = new Time();
                        if (ra.getFechaRegistro().equals((time.fecha()))) {
                            contador++;
                        }
                    }
                    if (contador > parametro) {
                        resultData.setText("Ya se registro en esta fecha");

                        reloadActivity();

                    } else {
                        myRefRegistroEvento = databaseReference.child("RegistroActividad");
                        for (int i = 0; i < list.size(); i++) {
                            RegistroActividad ra = list.get(i);
                            String key = String.valueOf(ra.getIdRegistro());
                            ra.setVisibilidad("0");
                            myRefRegistroEvento.child(key).setValue(ra);
                        }
                        //creo el nodo Registro actividad
                        crearRegistro(objSnapshot, result, claveActPer, idRegistro);
                    }
                } else {
                    if (processDone == 0) {
                        processDone++;
                        //creo el nodo Registro actividad
                        crearRegistro(objSnapshot, result, claveActPer, idRegistro);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRefRegistroEvento.orderByChild("idAct_idPer").equalTo(claveActPer).addValueEventListener(valueEventListener);
        mSendEventListner = valueEventListener;
        //vibra el cel
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        vibrator.vibrate(500);
    }


    private void crearRegistro(DataSnapshot objSnapshot, Result result, String claveActPer, String idRegistro) {
        if (latLng != null && isWithin10km == false) {
            resultData.setText("Fuera de rango del evento");
            reloadActivity();
        }
        if (latLng != null && isWithin10km == true) {
            RegistroActividad registroActividad = (RegistroActividad) CrearObjetoRegistro(objSnapshot, result, claveActPer, idRegistro);
            final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("RegistroActividad");
            myRef2.getRef().child(idRegistro).setValue(registroActividad);
            resultData.setText("Registro Exitoso");
            reloadActivity();
        }
        if (latLng == null) {
            RegistroActividad registroActividad2 = (RegistroActividad) CrearObjetoRegistro(objSnapshot, result, claveActPer, idRegistro);
            final DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("RegistroActividad");
            myRef3.getRef().child(idRegistro).setValue(registroActividad2);
            resultData.setText("Registro Exitoso null");
            reloadActivity();
        }
    }

    private Object CrearObjetoRegistro(DataSnapshot objSnapshot, Result result, String actPer, String idRegistro) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String nombreActividad = (String) objSnapshot.child("nombre").getValue();
        String lugarActividad = (String) objSnapshot.child("lugar").getValue();
        String imagenUrl = (String) objSnapshot.child("urlImagen").getValue();
        Time time = new Time();
        // En este momento el usuario toma una copia
        // y se crea un nuevo registro
        // tengo que validar que en la misma fecha no se registre mas de una vez
        // o llevar por parametro las veces que se necesita tomar asistencia

        RegistroActividad registroActividad = new RegistroActividad();
        registroActividad.setIdRegistro(idRegistro);
        registroActividad.setNombreActividad(nombreActividad);
        registroActividad.setLugarActividad(lugarActividad);
        registroActividad.setIdActividad(result.getText());
        registroActividad.setIdPersona(user.getUid());
        registroActividad.setHoraRegistro(time.hora());
        registroActividad.setFechaRegistro(time.fecha());
        registroActividad.setImagenActividad(imagenUrl);
        registroActividad.setVisibilidad("1");
        registroActividad.setIdAct_idPer(actPer);
        //decision
        return registroActividad;
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
        Intent intent = new Intent(QRScanner.this, MainActivity.class);
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
