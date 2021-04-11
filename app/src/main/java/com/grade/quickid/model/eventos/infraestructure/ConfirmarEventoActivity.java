package com.grade.quickid.model.eventos.infraestructure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grade.quickid.R;
import com.grade.quickid.model.estadisticas.aplication.CrearEstadistica;
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.registros.aplication.ActualizarRegistros;

import java.io.Serializable;

public class ConfirmarEventoActivity extends AppCompatActivity implements Serializable {
    private Button btn_CrearEvento;
    private Button btn_CancelarEvento;
    TextView titulo;
    DatabaseReference databaseReference;
    DatabaseReference myRef;
    DatabaseReference myRef2;
    Uri mImageUri;
    private ProgressDialog mProgress;
    private StorageReference mStorageRef;
    FirebaseDatabase firebaseDatabase;
    Evento receive;
    String imagenOriginal;
    CrearEstadistica crearEstadistica = new CrearEstadistica();
    private int update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_evento);
        titulo = (TextView) findViewById(R.id.txt_tituloConfirmarEvento);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mProgress = new ProgressDialog(this);
        btn_CrearEvento = (Button) findViewById(R.id.btn_crearEvento);
        btn_CancelarEvento = (Button) findViewById(R.id.btn_cancelarEvento);
        inicializarFirebase();
        receive = (Evento) getIntent().getSerializableExtra("Evento");
        mImageUri = Uri.parse(receive.getUrlImagen());
        imagenOriginal = getIntent().getStringExtra("Original");
        update = getIntent().getIntExtra("Update", 0);
        if (update != 0) {
            titulo.setText("Actualización evento terminada!");
            btn_CrearEvento.setText("Actualizar");
        }
        btn_CancelarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConfirmarEventoActivity.this, "Se ha cancelado la configuracion del evento", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // Closing all the Activities, clear the back stack.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        btn_CrearEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mImageUri != null) {
                    if (update == 0) {
                        mProgress.setMessage("Creando Evento");
                    } else {
                        mProgress.setMessage("Actualizando Evento");
                    }
                    mProgress.show();
                    if (mImageUri.toString().equals(imagenOriginal)) {
                        actualizarEventoImagenOriginal(imagenOriginal);
                        mProgress.dismiss();
                        Toast.makeText(ConfirmarEventoActivity.this, "Evento Actualizado Satisfactoriamente", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // Closing all the Activities, clear the back stack.
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {

                        final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
                        fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        actualizarEventoImagenChanged(uri.toString());
                                        mProgress.dismiss();
                                        Toast.makeText(ConfirmarEventoActivity.this, "Ok", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        // Closing all the Activities, clear the back stack.
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                });

                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ConfirmarEventoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            }
                        });
                    }
                } else {
                    Toast.makeText(ConfirmarEventoActivity.this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void actualizarEventoImagenOriginal(String Uri) {
        myRef = databaseReference.child("Evento");
        receive.setUrlImagen(Uri);
        ActualizarRegistros actualizarRegistros = new ActualizarRegistros();
        actualizarRegistros.ActualizarRegistros(receive, this.getApplicationContext());
        myRef.child(receive.getIdEvento()).setValue(receive);
        finish();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void actualizarEventoImagenChanged(String Uri) {
        // inserto el objeto Registro
        myRef = databaseReference.child("Evento");
        myRef2 = databaseReference.child("Estadistica");
        receive.setUrlImagen(Uri);
        ActualizarRegistros actualizarRegistros = new ActualizarRegistros();
        actualizarRegistros.ActualizarRegistros(receive, this.getApplicationContext());
        myRef.child(receive.getIdEvento()).setValue(receive);
        finish();
    }
}
