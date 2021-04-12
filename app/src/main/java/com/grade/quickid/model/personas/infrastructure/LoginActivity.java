package com.grade.quickid.model.personas.infrastructure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.google.firebase.auth.GoogleAuthProvider;
import com.grade.quickid.R;
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.personas.application.PersonaController;

/**
 * Clase que realiza el Login de la aplicacion mediante Google Sign in
 *
 * @author Cristian Camilo Soto
 */
public class LoginActivity extends AppCompatActivity {
    private Button mGoogleBtn;
    private int GOOGLE_SIGN_IN = 100;
    private GoogleSignInAccount account;
    private String email;
    private String imagen;
    private String nombre;

    PersonaController personaController = new PersonaController();

    /**
     * Se cargan los componentes del layout
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        mGoogleBtn = (Button) findViewById(R.id.googlebtn);
        session();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                            GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("325202218608-vlc12f68e2mco7donpnpe5mupvt7uie4.apps.googleusercontent.com")
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

    /**
     * se asegura la persistencia de la session al momento de cerrar y volver a abrir la app
     */
    private void session() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        email = prefs.getString("email", null);
        nombre = prefs.getString("nombre", null);
        imagen = prefs.getString("imagen", null);
        if (email != null) {
            Log.d("MAIL", email);
        }
        if (email != null) {
            showMain();
            finish();

        }
    }

    /**
     * al iniciar la app sea visible
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.setVisible(true);
    }

    /**
     * por si sucede error
     */
    private void ShowAlert(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Regresando..."+ task.getException()+ " "+ task.getResult());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * vamos a la clase main
     */
    private void showMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("nombre", nombre);
        intent.putExtra("imagen", imagen);
        startActivity(intent);
        finish();
    }

    /**
     * se controla el login de Google
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                                        email = account.getEmail().toString();
                                        imagen = account.getPhotoUrl().toString();
                                        nombre = account.getDisplayName();
                                        personaController.crearPersona(account,task);
                                        showMain();
                                    } else {
                                        ShowAlert(task);
                                    }
                                }
                            }
                    );
                }
            } catch (ApiException e) {
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Errror");
                builder.setMessage("Se ha producido un error autenticando al usuario" + e.getMessage());
                AlertDialog dialog = builder.create();
                // dialog.show();
            }
        }
    }
}
