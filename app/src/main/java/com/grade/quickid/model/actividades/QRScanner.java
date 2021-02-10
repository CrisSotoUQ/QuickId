package com.grade.quickid.model.actividades;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
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
import com.grade.quickid.model.registroActividad.RegistroActividad;
import com.grade.quickid.model.Time;

import java.util.ArrayList;
import java.util.UUID;

public class QRScanner extends AppCompatActivity {
    CodeScanner mCodeScanner;
    CodeScannerView scannView;
    TextView resultData;
    static int OnScannerElse;
    DatabaseReference databaseReference;
    DatabaseReference myRefRegistroEvento;
    DatabaseReference myRefEvento;
    FirebaseDatabase firebaseDatabase;
    int processDone = 0;
    ValueEventListener mSendEventListner;
    ValueEventListener mSendEventListner2;
    int contadorMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);
        OnScannerElse = 0;
        scannView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannView);
        resultData = findViewById(R.id.txtResult);
        resultData.setText("");
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
                                            String cargaStatus= objSnapshot.child("cargueArchivoStatus").getValue(String.class);
                                            if (cargaStatus.equals("1")) {
                                                for(String value : act.getListaPersonas().values()){
                                                    if (user.getEmail().equals(value)) {
                                                        Toast.makeText(QRScanner.this,"Se ha encontrado",Toast.LENGTH_SHORT);
                                                        resultData.setText("Encontrado");
                                                        contadorMatch++;
                                                        break;
                                                    }
                                                }
                                                if(contadorMatch>0){
                                                    siguienteSnapshot(objSnapshot, result, claveActPer, idRegistro);
                                                }else{
                                                    Toast.makeText(QRScanner.this,"Usuario no existe en la lista",Toast.LENGTH_SHORT);
                                                }
                                            }else{
                                                   siguienteSnapshot(objSnapshot, result, claveActPer, idRegistro);
                                            }

                                        }
                                    } else {
                                        resultData.setText("El evento no existe");
                                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(500);
                                        vibrator.vibrate(500);
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        }, 1500);
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
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    startActivity(getIntent());
                                }
                            }, 1500);
                        }

                    }
                });
            }
        });
    }

    private void siguienteSnapshot(DataSnapshot objSnapshot, Result result, String claveActPer, String idRegistro) {
        FirebaseDatabase firebaseDatabase3 = FirebaseDatabase.getInstance();
        myRefRegistroEvento = firebaseDatabase3.getInstance().getReference().child("RegistroActividad");

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
                        resultData.setText("Ya esta registrado en esta fecha");
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                startActivity(getIntent());
                            }
                        }, 1000);

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
        RegistroActividad registroActividad = (RegistroActividad) CrearObjetoRegistro(objSnapshot, result, claveActPer, idRegistro);
        final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("RegistroActividad");
        myRef2.getRef().child(idRegistro).setValue(registroActividad);
        final Handler handler = new Handler(Looper.getMainLooper());
        resultData.setText("Registro Exitoso");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
            }
        }, 1500);
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
        // el registro ajuste que se realizara mas adelante

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
        //codigo adicional
        this.finish();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }


}
