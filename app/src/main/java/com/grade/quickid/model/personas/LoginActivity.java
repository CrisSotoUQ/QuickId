package com.grade.quickid.model.personas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grade.quickid.R;
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.actividades.ConfirmarEvento;
import com.grade.quickid.model.actividades.CrearEventoActivity;
import com.grade.quickid.model.personas.domain.Persona;

public class LoginActivity extends AppCompatActivity {
    private Button mAccederBtn, mRegistrarBtn, mGoogleBtn;
    private EditText txt_emailLogin, txt_contrasenaLogin;
    private int GOOGLE_SIGN_IN = 100;
    GoogleSignInAccount account;
    private String email;
    private String imagen;
    private String nombre;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mGoogleBtn = (Button) findViewById(R.id.googlebtn);
        session();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                            GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("325202218608-omid265niarr31ht6de06sp1nqni6td2.apps.googleusercontent.com")
                            .requestEmail()
                            .build();
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
                    googleSignInClient.signOut();

                    startActivityForResult(googleSignInClient.getSignInIntent(), GOOGLE_SIGN_IN);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void session() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        email = prefs.getString("email", null);
        nombre = prefs.getString("nombre", null);
        imagen = prefs.getString("imagen", null);
        if (email != null) {
            this.setVisible(false);
            ShowMain();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setVisible(true);
    }


    private void ShowAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Proceso cancelado");
        builder.setMessage("Regresando...");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ShowMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("nombre", nombre);
        intent.putExtra("imagen", imagen);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account = task.getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Persona/" + user.getUid());
                                        final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Persona").child(user.getUid());
                                        myRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    email = account.getEmail();
                                                    nombre = account.getDisplayName();
                                                    imagen = account.getPhotoUrl().toString();
                                                    ShowMain();
                                                } else {
                                                    Persona persona = new Persona();
                                                    persona.setId(user.getUid());
                                                    persona.setNombre(account.getDisplayName());
                                                    persona.setApellido(account.getFamilyName());
                                                    persona.setImagenUri(account.getPhotoUrl().toString());
                                                    persona.setCorreo(account.getEmail());
                                                    myRef2.setValue(persona);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                System.out.println("Fallo la lectura: " + databaseError.getCode());
                                            }
                                        });
                                    } else {
                                        ShowAlert();
                                    }
                                }
                            }
                    );
                }
            } catch (ApiException e) {
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("S");
                builder.setMessage("Se ha producido un error autenticando al usuario2" + e.getMessage());
                AlertDialog dialog = builder.create();
                // dialog.show();
            }
        }
    }
}
  /*
        mRegistrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_emailLogin.getText().equals("") || txt_contrasenaLogin.getText().equals("")) {
                    Toast.makeText(LoginActivity.this, "Validar los campos", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(txt_emailLogin.getText().toString(),
                            txt_contrasenaLogin.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final DatabaseReference myRef =  FirebaseDatabase.getInstance().getReference("Persona/"+user.getUid());
                                final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Persona").child(user.getUid());
                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            ShowMain(task.getResult().getUser().getEmail(),"Normal",null
                                                    ,task.getResult().getUser().getDisplayName());
                                        }else{
                                            Persona persona= new Persona();
                                            persona.setId(user.getUid());
                                            persona.setCorreo(task.getResult().getUser().getEmail());
                                            persona.setNombre(task.getResult().getUser().getDisplayName());
                                            myRef2.setValue(persona);
                                            //m,

                                        }                       }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        System.out.println("Fallo la lectura: " + databaseError.getCode());
                                    }
                                });
                            } else {
                                ShowAlert();
                            }

                        }
                    });

                }
            }
        });*/

   /*     mAccederBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_emailLogin.getText().equals("") || txt_contrasenaLogin.getText().equals("")) {
                    Toast.makeText(LoginActivity.this, "Validar los campos", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(txt_emailLogin.getText().toString(),
                            txt_contrasenaLogin.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ShowMain(task.getResult().getUser().getEmail(), "Invitado",null,task.getResult().getUser().getDisplayName());
                            } else {
                                ShowAlert();
                            }

                        }
                    });

                }
            }
        });*/