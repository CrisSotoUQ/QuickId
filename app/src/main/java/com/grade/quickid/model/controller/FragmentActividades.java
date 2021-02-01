package com.grade.quickid.model.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
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
import com.grade.quickid.BuildConfig;
import com.grade.quickid.R;
import com.grade.quickid.model.Actividad;
import com.grade.quickid.model.CrearEventoActivity;
import com.grade.quickid.model.Persona;
import com.grade.quickid.model.QRGenerator;

import com.grade.quickid.model.RegistroActividad;
import com.grade.quickid.model.adaptadores.AdapterActividades;
import java.io.File;
import java.io.FileOutputStream;
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
    DatabaseReference myRefDatosRegistroEvento;
    DatabaseReference myRefDatosPersonaEvento;
    private Button btnEliminar,btnEditar,btnDescargarDatos;
    ValueEventListener eventListner;
    ValueEventListener eventListner1;
    private Context context;
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

        btnDescargarDatos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
        StringBuilder data = new StringBuilder();

            FirebaseDatabase firebaseDatabase3 = FirebaseDatabase.getInstance();
            myRefDatosRegistroEvento = firebaseDatabase3.getInstance().getReference().child("RegistroActividad");

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        data.append("Nombre Evento: "+actividad.getNombre());
                        data.append("\n"+"Lugar Evento: "+actividad.getLugar());
                        data.append("\n");
                        data.append("\n"+"Correo ,   Apellido  ,   Nombre   , Fecha  ,  Hora entrada ,Hora salida");
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        myRefDatosPersonaEvento = firebaseDatabase.getInstance().getReference().child("Persona");

                            for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                RegistroActividad ra = objSnapshot.getValue(RegistroActividad.class);

                                ValueEventListener valueEventListener1 = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        for (DataSnapshot objSnapshot : snapshot1.getChildren()) {
                                        Persona per = objSnapshot.getValue(Persona.class);
                                        data.append("\n" + String.valueOf(per.getCorreo()) + "," + String.valueOf(per.getApellido())
                                                + "," +  String.valueOf(per.getNombre())   + "," +  String.valueOf(ra.getFechaRegistro())
                                                + "," +  String.valueOf(ra.getHoraRegistro())   + "," +  String.valueOf(ra.getHoraRegistro()));
                                    }
                                        try {

                                            if (data!= null) {
                                                FileOutputStream out = getContext().openFileOutput("data.csv", Context.MODE_PRIVATE);
                                                out.write((data.toString()).getBytes());
                                                out.close();
                                                Context context = getContext();
                                                File filelocation = new File(context.getFilesDir(), "data.csv");
                                                Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", filelocation);

                                                Intent fileIntent = new Intent(Intent.ACTION_SEND);
                                                fileIntent.setType("text/csv");
                                                fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
                                                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                                                dialog.dismiss();
                                                startActivity(Intent.createChooser(fileIntent, "Send mail"));
                                                myRefDatosRegistroEvento.removeEventListener(eventListner);
                                                myRefDatosRegistroEvento.removeEventListener(eventListner1);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                myRefDatosPersonaEvento.orderByChild("id").equalTo(ra.getIdPersona()).addValueEventListener(valueEventListener1);
                                eventListner1 =  valueEventListener1;

                            }

                    }

                    }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
                myRefDatosRegistroEvento.orderByChild("idActividad").equalTo(actividad.getIdActividad()).addValueEventListener(valueEventListener);
                eventListner =  valueEventListener;

            }
        });

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