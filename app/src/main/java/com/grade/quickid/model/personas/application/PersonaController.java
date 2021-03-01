package com.grade.quickid.model.personas.application;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grade.quickid.model.personas.domain.Persona;

/**
 * Clase que controla las acciones para Persnas
 * @author  Cristian Camilo Soto
 */
public class PersonaController {
    private DatabaseReference myRefPersona;
    private ValueEventListener mEventListenerPersona;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public void crearPersona(GoogleSignInAccount account, Task<AuthResult> task) {

        ValueEventListener valueEventListenerPersona = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //si la persona no existe se crea
                if (!dataSnapshot.exists()) {
                    Persona persona = new Persona();
                    persona.setId(task.getResult().getUser().getUid());
                    persona.setNombre(account.getDisplayName());
                    persona.setApellido(account.getFamilyName());
                    persona.setImagenUri(account.getPhotoUrl().toString());
                    persona.setCorreo(account.getEmail());
                    myRefPersona.getDatabase().getReference().child("Persona").child(task.getResult().getUser().getUid()).setValue(persona);
                    myRefPersona.removeEventListener(mEventListenerPersona);
                }
                myRefPersona.removeEventListener(mEventListenerPersona);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        };
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRefPersona = firebaseDatabase.getInstance().getReference().child("Persona");
        myRefPersona.child(task.getResult().getUser().getUid()).addValueEventListener(valueEventListenerPersona);
        mEventListenerPersona = valueEventListenerPersona;

    }
}
