package com.grade.quickid.model;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.grade.quickid.R;
import com.grade.quickid.model.controller.FragmentPagerController;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.UUID;

public class CrearEventoActivity extends AppCompatActivity implements Serializable {
    FragmentPagerController pagerAdapter;
    private EditText txt_nombreActividad,txt_nombreLugar;
    public String claveQR = "23";
    private AlertDialog dialog;
    Uri mImageUri;

    private Button btn_galeria;
    private Button btn_camara;
    private Button btn_dialog_subirImagen;
    private Button btn_cancelar;
    private ImageView imageview_photo;
    private Button btn_siguiente;
    public Bitmap image ;
    private Switch mapSwitch;
    private static final int CAMERA_INTENT= 0;
    private static final int GALLERY_INTENT = 1;
    private AlertDialog.Builder dialogBuilder;
    private  Switch turnGeolocalizacion;
    private String activarGeolocalizacion = "0";
    private int CAMERA_PERMISSION_CODE = 1;
    String nombreActividad = null;
    String nombreLugar = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);
        mapSwitch = findViewById(R.id.switch1_geolocalizacion);
        imageview_photo = findViewById(R.id.imageView_nuevoEvento);
        btn_dialog_subirImagen = findViewById(R.id.btn_cargarImagen);
        txt_nombreActividad = (EditText) findViewById(R.id.txt_nombreActividad);
        txt_nombreLugar = (EditText) findViewById(R.id.txt_lugarActividad);
        btn_siguiente = findViewById(R.id.btn_Siguiente);
        setup();
        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 nombreActividad = txt_nombreActividad.getText().toString();
                 nombreLugar = txt_nombreLugar.getText().toString();

                if (activarGeolocalizacion.equals("0") ) {
                    if(nombreActividad.equals("")||nombreLugar.equals("")||mImageUri==null){
                        validacion();
                        return;
                    }else {

                        Actividad actividad = (Actividad) retornoObjetoActividad();
                        // envio en el intent
                        Intent act = new Intent(CrearEventoActivity.this, ConfirmarEvento.class);
                        act.putExtra("Actividad", actividad);
                        startActivity(act);
                    }
                }else{
                    if(nombreActividad.equals("")||nombreLugar.equals("") ||mImageUri==null){
                        validacion();
                        return;
                    }else{
                    Actividad actividad = (Actividad) retornoObjetoActividad();
                    // envio en el intent
                    Intent act = new Intent(CrearEventoActivity.this,MapsActivity.class);
                    act.putExtra("Actividad", actividad);
                    startActivity(act);
                }}
            }
        });
        turnGeolocalizacion = (Switch) findViewById(R.id.switch1_geolocalizacion);
        turnGeolocalizacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activarGeolocalizacion ="1";
                } else {
                    activarGeolocalizacion = "0";
                }
            }
        });

        btn_dialog_subirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialog(v);
            }
        });
    }

    private Object retornoObjetoActividad() {
        Time time = new Time();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = claveQR + UUID.randomUUID().toString();
        Actividad actividad = new Actividad();
        actividad.setIdActividad(id);
        actividad.setNombre(txt_nombreActividad.getText().toString());
        actividad.setLugar(txt_nombreLugar.getText().toString());
        actividad.setfIni(time.fecha());
        actividad.setId_persona(user.getUid());
        actividad.setEstadoActividad(null);
        actividad.setUrlImagen(mImageUri.toString());
        actividad.setGeolocStatus(activarGeolocalizacion);
        return actividad;
    }

    private void mostrarDialog(View v) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popCrearEventoFoto = getLayoutInflater().inflate(R.layout.popup_dialog_upload_images, null);
        btn_galeria = (Button) popCrearEventoFoto.findViewById(R.id.btn_galeria);
        btn_camara = (Button) popCrearEventoFoto.findViewById(R.id.btn_camara);
        btn_cancelar = (Button) popCrearEventoFoto.findViewById(R.id.btn_cancelarFoto);
        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CrearEventoActivity.this,
                        Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera,CAMERA_INTENT);
                    dialog.dismiss();
                }else {
                    requestCameraPermission();
                }
            }
        });

        btn_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                dialog.dismiss();
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setView(popCrearEventoFoto);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void openFileChooser() {
        Intent  intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
             mImageUri = data.getData();
             imageview_photo.setVisibility(View.VISIBLE);
             imageview_photo.setImageURI(mImageUri);
        }
        if (requestCode == CAMERA_INTENT && resultCode == RESULT_OK ) {
            image = (Bitmap) data.getExtras().get("data");
            imageview_photo.setVisibility(View.VISIBLE);
            imageview_photo.setImageBitmap(image);
            mImageUri = getImageUri(this, image);
    }}

    private void validacion() {
        if (nombreLugar.equals("")){
            txt_nombreLugar.setError("Requerido");
        }
        if (nombreActividad.equals("")) {
            txt_nombreActividad.setError("Requerido");
        }
        if (mImageUri ==null){
            Toast.makeText(CrearEventoActivity.this, "Imagen Requerida", Toast.LENGTH_LONG).show();
    }
    }

    private void setup() {
        setTitle("Crear Evento");
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==CAMERA_PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Abriendo camara",Toast.LENGTH_SHORT);
            }else{
                Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            new  android.app.AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Este permiso es requerido")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CrearEventoActivity.this,new String[]{ Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //codigo adicional
        this.finish();
    }

}
