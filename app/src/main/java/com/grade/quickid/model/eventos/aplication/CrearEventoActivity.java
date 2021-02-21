package com.grade.quickid.model.eventos.aplication;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
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
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.Time;
import com.grade.quickid.model.FragmentPagerController;
import com.grade.quickid.model.eventos.domain.Evento;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

public class CrearEventoActivity extends AppCompatActivity implements Serializable {
    FragmentPagerController pagerAdapter;
    private EditText txt_nombreActividad, txt_nombreLugar;
    public String claveQR = "23";
    private AlertDialog dialog;
    Uri mImageUri;

    private Button btn_galeria;
    private Button btn_camara;
    private Button btn_dialog_subirImagen;
    private Button btn_cancelar;
    private ImageView imageview_photo;
    private Button btn_siguiente;
    private ImageButton btn_TimePicker;
    public Bitmap image;
    private Switch mapSwitch;
    private static final int CAMERA_INTENT = 0;
    private static final int GALLERY_INTENT = 1;
    private AlertDialog.Builder dialogBuilder;
    private Switch switchGeolocalizacion;
    private Switch switchCargueCsv;
    private String activarGeolocalizacion = "0";
    private String activarCargueCsv = "0";
    private int CAMERA_PERMISSION_CODE = 1;
    private String nombreActividad = null;
    private String nombreLugar = null;
    private Evento receiveEvento;
    private String imagenOriginal;
    private int update;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    //Widgets
    EditText etHora;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);
        mapSwitch = findViewById(R.id.switch1_geolocalizacion);
        switchCargueCsv = findViewById(R.id.switch1_cargacsv);
        imageview_photo = findViewById(R.id.imageView_nuevoEvento);
        btn_dialog_subirImagen = findViewById(R.id.btn_cargarImagen);
        etHora = findViewById(R.id.et_mostrar_hora_picker);
        txt_nombreActividad = (EditText) findViewById(R.id.txt_nombreActividad);
        txt_nombreLugar = (EditText) findViewById(R.id.txt_lugarActividad);
        btn_siguiente = findViewById(R.id.btn_Siguiente);
        btn_TimePicker = findViewById(R.id.ib_time_picker);
        setup();
        Intent intent = getIntent();
        update = intent.getIntExtra("Update", 0);
        receiveEvento = (Evento) getIntent().getSerializableExtra("Evento");
        String imagen = intent.getStringExtra("imagen");
        if (update != 0) {
            txt_nombreActividad.setText(receiveEvento.getNombre().toString());
            txt_nombreLugar.setText(receiveEvento.getLugar().toString());
            Uri myUri = Uri.parse(receiveEvento.getUrlImagen());
            imageview_photo.setImageURI(myUri);
            imageview_photo.setVisibility(View.VISIBLE);
            mImageUri = myUri;

            Picasso.get().load(receiveEvento.getUrlImagen()).fit().centerInside().into(imageview_photo);
            imagenOriginal = receiveEvento.getUrlImagen();

            if (receiveEvento.getGeolocStatus().equals("0")) {
                mapSwitch.setChecked(false);
                activarGeolocalizacion = "0";
            } else {
                mapSwitch.setChecked(true);
                activarGeolocalizacion = "1";
            }
            if (receiveEvento.getCargueArchivoStatus().equals("0")) {
                switchCargueCsv.setChecked(false);
                activarCargueCsv = "0";
            } else {
                switchCargueCsv.setChecked(true);
                activarCargueCsv = "1";
            }
        }
        switchCargueCsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activarCargueCsv = "1";
                } else {
                    activarCargueCsv = "0";
                }
            }
        });
        btn_TimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerHora();
            }
        });
        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nombreActividad = txt_nombreActividad.getText().toString();
                nombreLugar = txt_nombreLugar.getText().toString();

                if (activarGeolocalizacion.equals("0") && activarCargueCsv.equals("0")) {
                    if (nombreActividad.equals("") || nombreLugar.equals("") || mImageUri == null) {
                        validacion();
                        return;
                    } else {
                        //si estamos en un update
                        if (update != 0) {
                            Intent act = new Intent(CrearEventoActivity.this, ConfirmarEvento.class);
                            retornoObjetoActividadUpdate();
                            act.putExtra("Evento", receiveEvento);
                            act.putExtra("Update", 1);
                            act.putExtra("Original", imagenOriginal);
                            startActivity(act);
                        } else {
                            Evento evento = (Evento) retornoObjetoActividad();
                            // envio en el intent a la ventana de confirmacion
                            Intent act = new Intent(CrearEventoActivity.this, ConfirmarEvento.class);
                            act.putExtra("Evento", evento);
                            startActivity(act);
                        }
                    }
                }  if (activarGeolocalizacion.equals("0") && activarCargueCsv.equals("1")){
                    if (nombreActividad.equals("") || nombreLugar.equals("") || mImageUri == null) {
                        validacion();
                        return;
                    } else {
                        //si estamos en un update
                        if (update != 0) {
                            Intent act = new Intent(CrearEventoActivity.this, CargarDatosCsv.class);
                            retornoObjetoActividadUpdate();
                            act.putExtra("Evento", receiveEvento);
                            act.putExtra("Update", 1);
                            act.putExtra("Original", imagenOriginal);
                            startActivity(act);
                            // si estamos creando
                        } else {
                            Evento evento = (Evento) retornoObjetoActividad();
                            // envio en el intent al maps
                            Intent act = new Intent(CrearEventoActivity.this, CargarDatosCsv.class);
                            act.putExtra("Evento", evento);
                            startActivity(act);
                        }
                    }
                }
              if (activarGeolocalizacion.equals("1") && activarCargueCsv.equals("0") ||
                      activarGeolocalizacion.equals("1") && activarCargueCsv.equals("1")){
                if (nombreActividad.equals("") || nombreLugar.equals("") || mImageUri == null) {
                    validacion();
                    return;
                } else {
                    //si estamos en un update
                    if (update != 0) {
                        Intent act = new Intent(CrearEventoActivity.this, MapsEventoActivity.class);
                        retornoObjetoActividadUpdate();
                        act.putExtra("Evento", receiveEvento);
                        act.putExtra("Update", 1);
                        act.putExtra("Original", imagenOriginal);
                        startActivity(act);
                        // si estamos creando
                    } else {
                        Evento evento = (Evento) retornoObjetoActividad();
                        // envio en el intent al maps
                        Intent act = new Intent(CrearEventoActivity.this, MapsEventoActivity.class);
                        act.putExtra("Evento", evento);
                        startActivity(act);
                    }
                }
            }

                }
        });
        switchGeolocalizacion = (Switch) findViewById(R.id.switch1_geolocalizacion);
        switchGeolocalizacion .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activarGeolocalizacion = "1";
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

    private void obtenerHora() {
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                String AM_PM;
                if (hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                //Muestro la hora con el formato deseado
                etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);

        recogerHora.show();
    }

    private void retornoObjetoActividadUpdate() {
        receiveEvento.setGeolocStatus(activarGeolocalizacion);
        receiveEvento.setCargueArchivoStatus(activarCargueCsv);
        receiveEvento.setNombre(txt_nombreActividad.getText().toString());
        receiveEvento.setLugar(txt_nombreLugar.getText().toString());
        receiveEvento.setUrlImagen(mImageUri.toString());
        receiveEvento.setHoraIni(etHora.getText().toString());
    }

    private Object retornoObjetoActividad() {
        Time time = new Time();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = claveQR + UUID.randomUUID().toString();
        Evento evento = new Evento();
        evento.setIdActividad(id);
        evento.setNombre(txt_nombreActividad.getText().toString());
        evento.setLugar(txt_nombreLugar.getText().toString());
        evento.setfIni(time.fecha());
        evento.setId_persona(user.getUid());
        evento.setEstadoActividad(null);
        evento.setUrlImagen(mImageUri.toString());
        evento.setGeolocStatus(activarGeolocalizacion);
        evento.setCargueArchivoStatus(activarCargueCsv);
        return evento;
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
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera, CAMERA_INTENT);
                    dialog.dismiss();
                } else {
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            imageview_photo.setVisibility(View.VISIBLE);
            imageview_photo.setImageURI(mImageUri);
        }
        if (requestCode == CAMERA_INTENT && resultCode == RESULT_OK) {
            image = (Bitmap) data.getExtras().get("data");
            imageview_photo.setVisibility(View.VISIBLE);
            imageview_photo.setImageBitmap(image);
            mImageUri = getImageUri(this, image);
        }
    }

    private void validacion() {
        if (nombreLugar.equals("")) {
            txt_nombreLugar.setError("Requerido");
        }
        if (nombreActividad.equals("")) {
            txt_nombreActividad.setError("Requerido");
        }
        if (mImageUri == null) {
            Toast.makeText(CrearEventoActivity.this, "Imagen Requerida", Toast.LENGTH_LONG).show();
        }
    }

    private void setup() {
        setTitle("Crear Evento");
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,"IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Abriendo camara", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Este permiso es requerido")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CrearEventoActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //codigo adicional
        this.finish();
        Intent intent = new Intent(CrearEventoActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
