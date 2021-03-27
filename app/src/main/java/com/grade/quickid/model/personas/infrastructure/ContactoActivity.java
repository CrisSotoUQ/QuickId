package com.grade.quickid.model.personas.infrastructure;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.grade.quickid.R;
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.comentarios.infraestructure.ComentarioActivity;

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