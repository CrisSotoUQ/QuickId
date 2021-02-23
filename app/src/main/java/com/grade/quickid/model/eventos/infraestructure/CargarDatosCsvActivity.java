package com.grade.quickid.model.eventos.infraestructure;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grade.quickid.R;
import com.grade.quickid.model.eventos.domain.Evento;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CargarDatosCsvActivity extends AppCompatActivity {
    private Button btn_cargarCsv;
    private Button btn_siguiente;
    Evento receiveEvento;
    private int update;
    String imagenOriginal;
    TextView textViewData;
    String line = " ";
    private int READ_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_datos_csv);
        textViewData = findViewById(R.id.textView_csvResult);
        btn_cargarCsv = (Button) findViewById(R.id.buttonCsv);
        btn_siguiente = (Button) findViewById(R.id.btn_csvSiguiente);
        textViewData.setMovementMethod(new ScrollingMovementMethod());
        receiveEvento = (Evento) getIntent().getSerializableExtra("Evento");
        update = getIntent().getIntExtra("Update",0);
        String concat = " ";
        if (update==1&& receiveEvento.getCargueArchivoStatus().equals("1")){

            for (String value : receiveEvento.getListaPersonas().values()) {
                concat += "\r\n" +value;
            }
            textViewData.setText(concat);
        }
        imagenOriginal = getIntent().getStringExtra("Original");
        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(CargarDatosCsvActivity.this, ConfirmarEventoActivity.class);

                if (!receiveEvento.getListaPersonas().equals(null)){
                    act.putExtra("Evento", receiveEvento);
                    act.putExtra("Original",imagenOriginal);
                    if(update !=0){
                        act.putExtra("Update",1);
                    }
                    startActivity(act);

            }else{
                    Toast.makeText(CargarDatosCsvActivity.this,"Es necesario cargar el archivo CSV",Toast.LENGTH_LONG);
                }
            }
        });
        btn_cargarCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new  Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {

                    Cursor returnCursor = getContentResolver().query(uri, null,
                            null, null, null);
                    /*
                     * Get the column indexes of the data in the Cursor,
                     * move to the first row in the Cursor, get the data,
                     * and display it.
                     */
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    returnCursor.moveToFirst();
                    String nombre = returnCursor.getString(nameIndex) + " has been selected!";
                    Toast.makeText(CargarDatosCsvActivity.this,"nombre",Toast.LENGTH_LONG);

                    ProcesarData(uri);
                  //  textViewData.setText(returnCursor.getString(nameIndex) + " has been selected!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void ProcesarData(Uri uri) throws IOException {
        InputStream inputStream;
        File file = null;
        if (uri.getScheme().equals("file")) {
            file = new File(uri.toString());

            inputStream = new FileInputStream(file);
        } else {
            inputStream = this.getContentResolver().openInputStream(uri);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String concat = " ";
        int contador = 0;
        HashMap<String,String> hash = new HashMap<>();
        while ((line = br.readLine()) != null) {
            contador++;
            // do something with line from file
            receiveEvento.setListaPersonas(String.valueOf(contador),line);
            concat += "\r\n" +line;
        }
        textViewData.setText(concat);
        br.close();
    }

    }

