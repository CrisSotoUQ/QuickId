package com.grade.quickid.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

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

import java.util.UUID;

public class QRScanner extends AppCompatActivity {
        CodeScanner mCodeScanner;
        CodeScannerView scannView;
        TextView resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);

        scannView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannView);
        resultData= findViewById(R.id.txtResult);
        inicializarFirebase();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String idActividad = result.getText();
                        // valido el identificador de los codigos QR por ahora un numero por default para separarlos del resto
                        if (idActividad.substring(0, 2).equals("23")) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference2 = firebaseDatabase2.getReference();
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

                                            RegistroActividad registroActividad = new RegistroActividad();
                                            registroActividad.setIdRegistro(id);
                                            registroActividad.setNombreActividad(nombreActividad);
                                            registroActividad.setLugarActividad(lugarActividad);
                                            registroActividad.setIdActividad(result.getText());
                                            registroActividad.setIdPersona(user.getUid());
                                            registroActividad.setHoraRegistro(time.hora());
                                            registroActividad.setFechaRegistro(time.fecha());
                                            registroActividad.setImagenActividad(imagenUrl);
//decision

                                            final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("RegistroActividad");
                                            myRef2.getRef().child(id).setValue(registroActividad);
                                            //vibra el cel
                                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                            vibrator.vibrate(500);
                                            vibrator.vibrate(500);
                                            finish();
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