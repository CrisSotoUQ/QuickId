package com.grade.quickid.model.registros.infraestructure;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.registros.aplication.CrearRegistro;
import com.grade.quickid.model.registros.domain.Registro;
import com.grade.quickid.model.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private int contadorMatch = 0;
    static boolean isWithin100m;
    private static LatLng latLng;
    private static FusedLocationProviderClient client;
    CrearRegistro crearRegistro = new CrearRegistro();
    public static final int REQUEST_CHECK_SETTING = 1001;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    LocationManager locationManager;

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
        prenderGps();

        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        inicializarFirebase();
        obtenerLocation(1);
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
                        // default 23, con el fin de separarlos del resto de QRS
                        if (idActividad.substring(0, 2).equals("23")) {
                            mEventListenerEvento = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                            String claveEventoPersona = idActividad + "" + idUsuario;
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
                                                    try {
                                                        snapshotRegistro(evento, result, claveEventoPersona);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    resultData.setText("Correo no encontrado");
                                                    closeEventListener();
                                                    reloadActivity();
                                                    vibrar();
                                                    break;

                                                }
                                                // es una actividad sin lista predeterminada
                                            } else {
                                                try {
                                                    snapshotRegistro(evento, result, claveEventoPersona);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
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
                                    closeEventListener();
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
        if (ActivityCompat.checkSelfPermission(QRScannerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(QRScannerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    }
                }}};
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
     * @param result
     * @param claveActPer
     */
    private void snapshotRegistro(Evento evento, Result result, String claveActPer) throws ParseException {
        // obtener la hora actual
        Date dateHoraActual = new Date();
        DateFormat formatter = new SimpleDateFormat("H:mm");
        String StringHoraActual = formatter.format(dateHoraActual);
        Date HoraActualFormateada = (Date) formatter.parse(StringHoraActual);

        // obtener la hora de inicio del evento
        String horaEventoIni = evento.getHoraIni().substring(0, 5);
        Date dateEventoIni = (Date) formatter.parse(horaEventoIni);

        // obtener la hora fin del evento
        String horaEventoFin = evento.getHoraFin().substring(0, 5);
        Date dateEventoFin = (Date) formatter.parse(horaEventoFin);

        if (HoraActualFormateada.after(dateEventoIni) && HoraActualFormateada.before(dateEventoFin)) {
            String idRegistro = UUID.randomUUID().toString();
            calcularDistanciaEntreLocalizacionUsuarioYEvento(evento);

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
                                break;
                            }
                        }
                        // parametrizar a futuro cantidad de registros validado para un evento
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
                            gestionarRegistro(evento, result, claveActPer, idRegistro);
                        }
                    } else {
                        if (processDone == 0) {
                            processDone++;
                            //creo el nodo Registro evento
                            gestionarRegistro(evento, result, claveActPer, idRegistro);
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
        } else {
            resultData.setText("Hora registro no valida ");
            vibrar();
            reloadActivity();
            closeEventListener();
        }
    }

    /**
     * funcion que calcula la distancia entre dos puntos en el espacio
     * Evento evento
     */
    private void calcularDistanciaEntreLocalizacionUsuarioYEvento(Evento evento) {
        // valido que la variable global latlong este llena para encontrar la distancia entre dos puntos en el espacio
        if (latLng == null) {
            try {
                Location location = getLastKnownLocation();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }catch (Exception e){
                resultData.setText("Es necesario reiniciar la aplicación");
                Log.d("Error",e.getMessage());
            }
        }
        if (latLng != null) {
            double latEvento = evento.getLatitud();
            double longEvento = evento.getLongitud();
            float[] results = new float[1];
            Location.distanceBetween(latEvento, longEvento, latLng.latitude, latLng.longitude, results);
            float distanceInMeters = results[0];
            // se establecen 500 metros como distancia maxima, por temas de error en la captura
            isWithin100m = distanceInMeters < 500;
        }
    }

    private void gestionarRegistro(Evento evento, Result result, String claveActPer, String idRegistro) {
        String geoLocStatus = evento.geolocStatus;
        if (geoLocStatus.equals("1")) {
            if (latLng != null && isWithin100m == false) {
                resultData.setText("Fuera de rango del evento");
                vibrar();
                closeEventListeners();
                reloadActivity();
            }
            if (latLng != null && isWithin100m == true) {
                Registro registro = (Registro) crearRegistro.CrearObjetoRegistro(evento, result, claveActPer, idRegistro);
                final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Registro");
                myRef2.getRef().child(idRegistro).setValue(registro);
                resultData.setText("Registro Exitoso");
                closeEventListeners();
                Intent intent = new Intent(QRScannerActivity.this, MainActivity.class);
                intent.putExtra("tab", 1);
                startActivity(intent);
                vibrar();
                this.finish();
            }
        } else {
            Registro registro2 = (Registro) crearRegistro.CrearObjetoRegistro(evento, result, claveActPer, idRegistro);
            final DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Registro");
            myRef3.getRef().child(idRegistro).setValue(registro2);

            resultData.setText("Registro Exitoso");
            closeEventListeners();
            Intent intent = new Intent(QRScannerActivity.this, MainActivity.class);
            intent.putExtra("tab", 1);
            startActivity(intent);
            vibrar();
            this.finish();
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void prenderGps() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(QRScannerActivity.this, "Gps is on", Toast.LENGTH_LONG).show();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            try {
                                resolvableApiException.startResolutionForResult(QRScannerActivity.this, REQUEST_CHECK_SETTING);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTING) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(QRScannerActivity.this, "Gps activado", Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(QRScannerActivity.this, "El GPS es requerido y debe ser activado", Toast.LENGTH_LONG).show();
                    break;

            }
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;

        }
        Log.d("location ",bestLocation.getLatitude()+" "+bestLocation.getLongitude());
        return bestLocation;
    }


}
