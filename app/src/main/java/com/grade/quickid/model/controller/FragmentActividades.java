package com.grade.quickid.model.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grade.quickid.R;
import com.grade.quickid.model.Actividad;
import com.grade.quickid.model.ConfirmarEvento;
import com.grade.quickid.model.CrearEventoActivity;
import com.grade.quickid.model.QRGenerator;
import com.grade.quickid.model.adaptadores.AdapterActividades;

import java.util.ArrayList;

public class FragmentActividades extends Fragment {
    AdapterActividades adapterActividades;
    RecyclerView rvActividades;
    ArrayList<Actividad> listActividades = new ArrayList<Actividad>();
    Menu menu;
    private StorageReference mStorageRef;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    private Button btnPausar,btnEliminar,btnEditar,btnDescargarDatos;
    public FragmentActividades() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu=menu;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);
        rvActividades = (RecyclerView) view.findViewById(R.id.Recycler_actividades);
        listarDatos();
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        return view;
    }
    private void listarDatos() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
        databaseReference.child("Actividad").orderByChild("id_persona").equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listActividades.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Actividad p = objSnapshot.getValue(Actividad.class);
                    listActividades.add(p);
                    if(getActivity()!= null) {
                        rvActividades.setLayoutManager(new LinearLayoutManager(getActivity()));
                        adapterActividades = new AdapterActividades(getActivity(), listActividades);
                        adapterActividades.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String idActividad = listActividades.get(rvActividades.getChildAdapterPosition(v)).getIdActividad();
                                String nombreActividad = listActividades.get(rvActividades.getChildAdapterPosition(v)).getNombre();
                                String lugarActividad = listActividades.get(rvActividades.getChildAdapterPosition(v)).getIdActividad();
                                Intent intent = new Intent(getActivity(), QRGenerator.class);
                                intent.putExtra("idActividad",idActividad);
                                intent.putExtra("nombre",nombreActividad);
                                intent.putExtra("lugar",lugarActividad);
                                startActivity(intent);
                            }
                        });
                        adapterActividades.setOnLongClickListener(new View.OnLongClickListener() {
                            public boolean onLongClick(View v) {
                                mostrarDialog(listActividades.get(rvActividades.getChildAdapterPosition(v)));
                                return false;
                            }
                        });
                        rvActividades.setAdapter(adapterActividades);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }else{
            Toast.makeText(getActivity(),"usuario no encontrado",Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarDialog(Actividad actividad) {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View popActividadFragment = getLayoutInflater().inflate(R.layout.popup_dialog_actividades,null);
        btnEliminar = (Button) popActividadFragment.findViewById(R.id.btn_eliminar_actividad);
        btnEditar = (Button)   popActividadFragment.findViewById(R.id.btn_editar_actividad);
        btnDescargarDatos = (Button)   popActividadFragment.findViewById(R.id.btn_descargar_datos);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatos(actividad);
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getActivity());
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿Quieres eliminar la Actividad "+ actividad.getNombre()+" ? ");
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
                databaseReference.child("Actividad").child(actividad.getIdActividad()).removeValue();
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(actividad.getUrlImagen());
                mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Imagen borrada satisfactoriamente
                        Log.d("TAG", "onSuccess: deleted file");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // algun eror ocurrio

                        Log.d("TAG", "onFailure: did not delete file");
                    }
                });
                adapterActividades.notifyDataSetChanged();
                listActividades.remove(actividad);
                Toast t=Toast.makeText(getActivity(),"Se ha eliminado satisfactoriamente", Toast.LENGTH_LONG);
                t.show();
                dialog.dismiss();
            }


        });
        dialogBuilder.setView(popActividadFragment);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void actualizarDatos(Actividad actividad) {
        // envio en el intent
        Intent act = new Intent(getActivity(), CrearEventoActivity.class);
        act.putExtra("Actividad", actividad);
        act.putExtra("Update",1);
        startActivity(act);
        dialog.dismiss();
    }
}