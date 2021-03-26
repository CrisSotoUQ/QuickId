package com.grade.quickid.model.registros.aplication;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.registros.domain.Registro;

public class ActualizarRegistros {
    ValueEventListener valueEventListenerPersonaEvento;
    DatabaseReference myRefDatosPersonaEvento ;
    DatabaseReference myRef;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
  public  void ActualizarRegistros(Evento evento, Context context){
      firebaseDatabase = FirebaseDatabase.getInstance();
      // firebaseDatabase.setPersistenceEnabled(true);
      databaseReference= firebaseDatabase.getReference();
      valueEventListenerPersonaEvento = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot1) {
            if (snapshot1 != null) {
                for (DataSnapshot objSnapshot : snapshot1.getChildren()) {
                    Registro regi = objSnapshot.getValue(Registro.class);
                    regi.setImagenEvento(evento.getUrlImagen());
                    regi.setLugarEvento(evento.getLugar());
                    regi.setNombreEvento(evento.getNombre());
                    myRef = databaseReference.child("Registro");
                    myRef.child(regi.getIdRegistro()).setValue(regi);

                }
                myRefDatosPersonaEvento.removeEventListener(valueEventListenerPersonaEvento);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    myRefDatosPersonaEvento = firebaseDatabase.getInstance().getReference().child("Registro");
    myRefDatosPersonaEvento.orderByChild("idEvento").equalTo(evento.getIdEvento()).addValueEventListener(valueEventListenerPersonaEvento);
}}
