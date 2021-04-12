package com.grade.quickid.model.eventos.infraestructure.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import com.grade.quickid.model.eventos.aplication.CrearDatosCsv;
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.eventos.aplication.CrearEventoActivity;
import com.grade.quickid.model.estadisticas.infraestructure.QRGenAndStatisticsActivity;

import java.util.ArrayList;

public class FragmentEventos extends Fragment {
    private static android.app.AlertDialog.Builder dialogo2;
    private AdapterFragmentEvento adapterFragmentEvento;
    private RecyclerView rvActividades;
    private ArrayList<Evento> listActividades = new ArrayList<Evento>();
    private Menu menu;
    private StorageReference mStorageRef;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private Button btnEliminar, btnEditar, btnDescargarDatos;
    private CardView cardView;

    public FragmentEventos() {
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
        this.menu = menu;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);
        rvActividades = (RecyclerView) view.findViewById(R.id.Recycler_actividades);
        cardView = (CardView) view.findViewById(R.id.id_cardview);
        listarDatos();
        return view;
    }

    private void listarDatos() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            databaseReference.child("Evento").orderByChild("id_persona").equalTo(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listActividades.clear();
                            for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                                Evento p = objSnapshot.getValue(Evento.class);
                                listActividades.add(p);
                                if (getActivity() != null) {
                                    rvActividades.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    adapterFragmentEvento = new AdapterFragmentEvento(getActivity(), listActividades);
                                    adapterFragmentEvento.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String idEvento = listActividades.get(rvActividades.getChildAdapterPosition(v)).getIdEvento();
                                            String nombreActividad = listActividades.get(rvActividades.getChildAdapterPosition(v)).getNombre();
                                            String lugarActividad = listActividades.get(rvActividades.getChildAdapterPosition(v)).getIdEvento();
                                            Intent intent = new Intent(getActivity(), QRGenAndStatisticsActivity.class);
                                            intent.putExtra("idEvento", idEvento);
                                            intent.putExtra("nombre", nombreActividad);
                                            intent.putExtra("lugar", lugarActividad);
                                            startActivity(intent);
                                        }
                                    });
                                    adapterFragmentEvento.setOnLongClickListener(new View.OnLongClickListener() {
                                        public boolean onLongClick(View v) {
                                            mostrarDialog(listActividades.get(rvActividades.getChildAdapterPosition(v)));
                                            return false;
                                        }
                                    });
                                    rvActividades.setAdapter(adapterFragmentEvento);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            Toast.makeText(getActivity(), "usuario no encontrado", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarDialog(Evento evento) {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        final View popActividadFragment = getLayoutInflater().inflate(R.layout.popup_dialog_actividades, null);
        btnEliminar = (Button) popActividadFragment.findViewById(R.id.btn_eliminar_actividad);
        btnEditar = (Button) popActividadFragment.findViewById(R.id.btn_editar_actividad);
        btnDescargarDatos = (Button) popActividadFragment.findViewById(R.id.btn_descargar_datos);

        btnDescargarDatos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CrearDatosCsv crearDatosCsv = new CrearDatosCsv();
                crearDatosCsv.CrearDatosCsv(evento, getContext());
                dialogo2 = new android.app.AlertDialog.Builder(getActivity());
                dialog.dismiss();

            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatos(evento);
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getActivity());
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿Quieres eliminar la Evento " + evento.getNombre() + " ? ");
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
                databaseReference.child("Evento").child(evento.getIdEvento()).removeValue();
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(evento.getUrlImagen());
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
                adapterFragmentEvento.notifyDataSetChanged();
                listActividades.remove(evento);
                Toast t = Toast.makeText(getActivity(), "Se ha eliminado satisfactoriamente", Toast.LENGTH_LONG);
                t.show();
                dialog.dismiss();
            }


        });
        dialogBuilder.setView(popActividadFragment);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    public static void showAlertDialog(Evento evento) {
        dialogo2.setTitle("No se han encontrado datos");
        dialogo2.setMessage("Este evento no posee registros " + evento.getNombre());
        dialogo2.setCancelable(false);
        dialogo2.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo2, int id) {
                dialogo2.dismiss();
            }
        });
        dialogo2.show();
    }

    private void actualizarDatos(Evento evento) {
        // envio en el intent
        Intent act = new Intent(getActivity(), CrearEventoActivity.class);
        act.putExtra("Evento", evento);
        act.putExtra("Update", 1);
        startActivity(act);
        dialog.dismiss();
    }

}