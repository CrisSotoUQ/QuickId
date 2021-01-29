package com.grade.quickid.model.controller;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grade.quickid.R;
import com.grade.quickid.model.RegistroActividad;
import com.grade.quickid.model.adaptadores.AdapterRegistros;

import java.util.ArrayList;


public class FragmentRegistros extends Fragment {
    AdapterRegistros adapterRegistros;
    RecyclerView rvRegistros;
    ArrayList<RegistroActividad> listRegistroActividads = new ArrayList<RegistroActividad>();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Button btnEliminar;

    public FragmentRegistros() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registros, container, false);
        rvRegistros = (RecyclerView) view.findViewById(R.id.Recycler_registros);
        listarDatos();
        // Inflate the layout for this fragment
        return view;
    }
   private void listarDatos() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user !=null){
        databaseReference.child("RegistroActividad").orderByChild("idPersona").equalTo(user.getUid()).
                addValueEventListener(new ValueEventListener() {
            @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  listRegistroActividads.clear();
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        Log.d("usuarios",objSnapshot.toString());
                        RegistroActividad p = objSnapshot.getValue(RegistroActividad.class);
                        listRegistroActividads.add(p);
                }
                if(getActivity()!= null) {
                    rvRegistros.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapterRegistros = new AdapterRegistros(getActivity(), listRegistroActividads);
                    adapterRegistros.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            mostrarDialog(listRegistroActividads.get(rvRegistros.getChildAdapterPosition(v)));
                            return false;
                        }
                    });
                    rvRegistros.setAdapter(adapterRegistros);
                }
                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });}else{
            Toast.makeText(getActivity(),"Usuario no encontrado",Toast.LENGTH_LONG).show();
        }
   }

    private void mostrarDialog(RegistroActividad registroActividad) {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View popRegistroFragment = getLayoutInflater().inflate(R.layout.popup_dialog_registros,null);
        btnEliminar = (Button) popRegistroFragment.findViewById(R.id.btn_eliminar_registros);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getActivity());
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿Quieres eliminar el RegistroActividad "+ registroActividad.getNombreActividad() +" ? ");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        aceptar();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }

            public void aceptar() {
                databaseReference.child("RegistroActividad").child(registroActividad.getIdRegistro()).removeValue();
                adapterRegistros.notifyDataSetChanged();
                listRegistroActividads.remove(registroActividad);
                System.gc();
                Toast t=Toast.makeText(getActivity(),"Se ha eliminado satisfactoriamente", Toast.LENGTH_LONG);
                t.show();
                dialog.dismiss();
            }

        });
        dialogBuilder.setView(popRegistroFragment);
        dialog = dialogBuilder.create();
        dialog.show();
    }



}
