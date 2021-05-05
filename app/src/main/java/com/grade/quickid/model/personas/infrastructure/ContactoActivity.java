package com.grade.quickid.model.personas.infrastructure;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.grade.quickid.R;
import com.grade.quickid.model.MainActivity;

/**
 * Clase que controla la gestion de contacto del desarrollador
 *
 * @author Cristian Camilo Soto
 */
public class ContactoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ContactoActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}