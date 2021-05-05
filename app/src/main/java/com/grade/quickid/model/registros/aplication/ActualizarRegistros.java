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

/**
 * Clase que controla la actualizacion de registros cuando se modifican los datos de un evento
 *
 * @author Cristian Camilo Soto
 */
public class ActualizarRegistros {
    private ValueEventListener valueEventListenerPersonaEvento = null;
    private DatabaseReference myRefDatosPersonaEvento ;
    private DatabaseReference myRef;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
  public  void ActualizarRegistros(Evento evento, Context context){
      String idEvento = evento.getIdEvento();
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
    myRefDatosPersonaEvento.orderByChild("idEvento").equalTo(idEvento).addValueEventListener(valueEventListenerPersonaEvento);
}}
