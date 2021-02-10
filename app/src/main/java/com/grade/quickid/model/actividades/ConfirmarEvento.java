package com.grade.quickid.model.actividades;

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
import com.grade.quickid.model.MainActivity;

import java.io.Serializable;

public class ConfirmarEvento extends AppCompatActivity implements Serializable {
    private Button btn_CrearEvento;
    TextView titulo;
    DatabaseReference databaseReference;
    DatabaseReference myRef;
    Uri mImageUri;
    private ProgressDialog mProgress;
    private StorageReference mStorageRef;
    FirebaseDatabase firebaseDatabase;
    Actividad receive;
    DatabaseReference myRef2;
    String imagenOriginal;
    private int update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_evento);
        titulo = (TextView) findViewById(R.id.txt_tituloConfirmarEvento);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mProgress = new ProgressDialog(this);
        btn_CrearEvento = (Button) findViewById(R.id.btn_crearEvento);
        inicializarFirebase();
        receive = (Actividad) getIntent().getSerializableExtra("Actividad");
        mImageUri = Uri.parse(receive.getUrlImagen());
        imagenOriginal = getIntent().getStringExtra("Original");
        update = getIntent().getIntExtra("Update",0);
        if (update !=0){
            titulo.setText("Actualizacion evento terminada!");
            btn_CrearEvento.setText("Actualizar");
        }
        btn_CrearEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mImageUri != null) {
                    mProgress.setMessage("Creando Evento");
                    mProgress.show();
                     if(mImageUri.toString().equals(imagenOriginal)){
                         actualizarEvento(imagenOriginal);
                         mProgress.dismiss();
                         Toast.makeText(ConfirmarEvento.this, "Evento Actualizado Satisfactoriamente", Toast.LENGTH_LONG).show();
                         Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                         // Closing all the Activities, clear the back stack.
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(intent);
                     }else{

                        final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
                        fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        crearEvento(uri.toString());
                                        mProgress.dismiss();
                                        Toast.makeText(ConfirmarEvento.this, "Ok", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(ConfirmarEvento.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            }
                        });
                    }
                } else {
                    Toast.makeText(ConfirmarEvento.this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void actualizarEvento(String Uri) {

        myRef = databaseReference.child("Actividad");
        receive.setUrlImagen(Uri);
        myRef.child(receive.getIdActividad()).setValue(receive);

        finish();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // firebaseDatabase.setPersistenceEnabled(true);
        databaseReference= firebaseDatabase.getReference();
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime =  MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void crearEvento(String Uri) {
        // inserto el objeto RegistroActividad
        myRef = databaseReference.child("Actividad");
        receive.setUrlImagen(Uri);
        myRef.child(receive.getIdActividad()).setValue(receive);
        finish();
    }
}
