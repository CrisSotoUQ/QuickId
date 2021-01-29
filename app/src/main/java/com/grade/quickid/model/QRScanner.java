package com.grade.quickid.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
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

import java.util.HashMap;
import java.util.UUID;

public class QRScanner extends AppCompatActivity {
        CodeScanner mCodeScanner;
        CodeScannerView scannView;
        TextView resultData;
        static  int OnScannerElse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);
        OnScannerElse = 0;
        scannView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannView);
        resultData= findViewById(R.id.txtResult);
        resultData.setText("");
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
                        String idUsuario = user.getUid();
                        String idActividad = result.getText();
                        // valido el identificador de los codigos QR por ahora un numero por default para separarlos del resto
                        if (idActividad.substring(0, 2).equals("23")) {

                                //copio los datos de la actividad para el nuevo registro
                                databaseReference2.child("Actividad").orderByChild("idActividad").equalTo(
                                        result.getText()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        for (DataSnapshot objSnapshot : dataSnapshot2.getChildren()) {
                                            String nombreActividad = (String) objSnapshot.child("nombre").getValue();
                                            String lugarActividad = (String) objSnapshot.child("lugar").getValue();
                                            String imagenUrl = (String) objSnapshot.child("urlImagen").getValue();
                                            Time time = new Time();
                                            String id = UUID.randomUUID().toString();
                                            // En este momento el usuario toma una copia
                                            // y se crea un nuevo registro
                                            // tengo que validar que en la misma fecha no se registre mas de una vez
                                            // o llevar por parametro las veces que se necesita tomar asistencia
                                            // el registro ajuste que se realizara mas adelante
                                            String claveActPer = idActividad+""+idUsuario;
                                            RegistroActividad registroActividad = new RegistroActividad();
                                            registroActividad.setIdRegistro(id);
                                            registroActividad.setNombreActividad(nombreActividad);
                                            registroActividad.setLugarActividad(lugarActividad);
                                            registroActividad.setIdActividad(result.getText());
                                            registroActividad.setIdPersona(user.getUid());
                                            registroActividad.setHoraRegistro(time.hora());
                                            registroActividad.setFechaRegistro(time.fecha());
                                            registroActividad.setImagenActividad(imagenUrl);
                                            registroActividad.setVisibilidad("1");
                                            registroActividad.setIdAct_idPer(claveActPer);
                                            //decision
                                            FirebaseDatabase firebaseDatabase3 = FirebaseDatabase.getInstance();
                                            DatabaseReference databaseReference3 = firebaseDatabase3.getReference();

                                            databaseReference3.child("RegistroActividad").orderByChild("idAct_idPer")
                                                    .equalTo(claveActPer).addListenerForSingleValueEvent(
                                                    new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                    validacionNodos(claveActPer, registroActividad);
                                                            }else{
                                                                if (OnScannerElse == 0) {
                                                                    final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("RegistroActividad");
                                                                    myRef2.getRef().child(id).setValue(registroActividad);
                                                                    finish();
                                                                }
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    }
                                            );
                                            //vibra el cel
                                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                            vibrator.vibrate(500);
                                            vibrator.vibrate(500);

                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        System.out.println("Fallo la lectura: " + databaseError.getCode());
                                    }
                                });
                    }else{
                            resultData.setText("Codigo QR invalido");
                        }
                    }
                });
            }
        });
        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    private void validacionNodos(String id, RegistroActividad registroActividad) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("RegistroActividad").orderByChild("idAct_idPer")
                .equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    int contador =0;
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        String fechaRegistro = d.child("fechaRegistro").getValue(String.class);
                        if (fechaRegistro.equals(registroActividad.getFechaRegistro())) {
                            contador++;
                        }
                        if (contador > 0) {
                            resultData.setText("Ya estas registrado en esta fecha");
                            return;
                        } else {
                            actualizarVisibilidadNodos(id);
                        }
                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // quito la visibilidad de los registros anteriores
    private void actualizarVisibilidadNodos(String id) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
             DatabaseReference reference = firebaseDatabase.getReference();
            reference.child("RegistroActividad").orderByChild("idAct_idPer")
            .equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for(DataSnapshot d : dataSnapshot.getChildren()) {
                            Log.d("Keys",String.valueOf(d.getKey())); //returning all the keys
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("visibilidad", "0");
                            reference.child(String.valueOf(d.getKey())).updateChildren(result);  //update according to keys
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

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
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // firebaseDatabase.setPersistenceEnabled(true);
        DatabaseReference databaseReference = firebaseDatabase.getReference();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //codigo adicional
        this.finish();
    }
}