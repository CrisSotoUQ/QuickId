package com.grade.quickid.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grade.quickid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CrudActivity extends AppCompatActivity {
    EditText txt_nombre,txt_correo,txt_password,txt_apellido;
    ListView listView_personas;

    String nombre= null;
    String correo = null;
    String password= null;
    String apellido = null;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private List<Persona> listpersona = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;
    Persona personaSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        txt_nombre = findViewById(R.id.txt_nombre_persona);
        txt_correo = findViewById(R.id.txt_nombre_correo);
        txt_password = findViewById(R.id.txt_nombre_pass);
        txt_apellido = findViewById(R.id.txt_nombre_apellido);

        listView_personas = findViewById(R.id.lv_datosPersonas);
        inicializarFirebase();
        listarDatos();
        listView_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Persona) parent.getItemAtPosition(position);
                txt_nombre.setText(personaSelected.getNombre());
                txt_apellido.setText(personaSelected.getApellido());
                txt_correo.setText(personaSelected.getCorreo());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listpersona.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Persona p = objSnapshot.getValue(Persona.class);
                    listpersona.add(p);
                    arrayAdapterPersona = new ArrayAdapter<Persona>(CrudActivity.this, android.R.layout.simple_list_item_1,listpersona);
                    listView_personas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        nombre  = txt_nombre.getText().toString();
        correo = txt_correo.getText().toString();
        apellido = txt_apellido.getText().toString();
        password = txt_password.getText().toString();

        switch (item.getItemId()){
            case R.id.action_edit:{
                if (nombre.equals("")||correo.equals("")||apellido.equals("")||password.equals("")){
                    validacion();
                    break;
                }else{
                    Persona p= new  Persona();
                    p.setId(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellido(apellido);
                    p.setCorreo(correo);
                    databaseReference.child("Persona").child(p.getId()).setValue(p);
                    Toast.makeText(this,"Agregar",Toast.LENGTH_LONG).show();
                    limpiarCajas();
                    break;
                }

            }
            case R.id.action_save:{
                try{
                    if (personaSelected.getId() != null){
                    Persona p = new Persona();
                    p.setId(personaSelected.getId());
                    p.setCorreo(txt_correo.getText().toString().trim());
                    p.setNombre(txt_nombre.getText().toString().trim());
                    p.setApellido(txt_apellido.getText().toString().trim());
                    databaseReference.child("Persona").child(p.getId()).setValue(p);
                    Toast.makeText(this,"Guardar",Toast.LENGTH_LONG).show();
                    limpiarCajas();
                    break;
                    }else{
                        Toast.makeText(this,"No se puede actualizar la persona",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this,"No se puede actualizar la persona",Toast.LENGTH_LONG).show();
                }


            }
            case R.id.action_play:{
                try {
                    if (personaSelected.getId() != null){
                    Persona p = new Persona();
                    p.setId(personaSelected.getId());
                    databaseReference.child("Persona").child(p.getId()).removeValue();
                    limpiarCajas();
                    Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show();
                    break;
                    }else{
                        Toast.makeText(this,"No se puede eliminar la persona",Toast.LENGTH_LONG).show();
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(this,"No se puede eliminar la persona",Toast.LENGTH_LONG).show();

                }
            }
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void limpiarCajas() {
        txt_nombre.setText("");
        txt_password.setText("");
        txt_correo.setText("");
        txt_apellido.setText("");
    }

    private void validacion() {
        if (nombre.equals("")){
            txt_nombre.setError("Requerido");
        }
        if (apellido.equals("")){
            txt_apellido.setError("Requerido");
        }
        if (correo.equals("")){
            txt_correo.setError("Requerido");
        }
        if (password.equals("")){
            txt_password.setError("Requerido");
        }
    }

}