
package com.grade.quickid.model.comentarios.infraestructure;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grade.quickid.R;
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.Time;
import com.grade.quickid.model.comentarios.domain.Comentario;

import java.util.UUID;

/**
 * Clase que gestiona Generar comentarios
 *
 * @author Cristian Camilo Soto
 */
public class ComentarioActivity extends AppCompatActivity {
private Button enviarComentario;
private TextView textoComentario;
DatabaseReference myRef;
FirebaseDatabase firebaseDatabase;
DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);
        textoComentario = findViewById(R.id.textviewComentario);
        textoComentario.setHint(" Comparte tu comentario");
        enviarComentario = findViewById(R.id.btn_enviarComentario);
        inicializarFirebase();
        enviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String coment =  textoComentario.getText().toString();
                Comentario comentario = new Comentario();
                String clave= UUID.randomUUID().toString();
                if(coment != null && coment.length()>0){
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Time time = new Time();
                    comentario.setComentario(coment);
                    myRef = databaseReference.child("Comentario");
                    comentario.setFechaComentario(time.fecha());
                    comentario.setIdCOmentario(clave);
                    comentario.setIdUsuario(user.getUid()+" "+user.getDisplayName());
                    myRef.child(clave).setValue(comentario);
                    Toast.makeText(ComentarioActivity.this,"Gracias por tu comentario",Toast.LENGTH_LONG).show();
                    textoComentario.setText("");
                }else{
                    Toast.makeText(ComentarioActivity.this,"comentario vacio",Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // firebaseDatabase.setPersistenceEnabled(true);
        databaseReference= firebaseDatabase.getReference();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ComentarioActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}