package com.grade.quickid.model.registros.infraestructure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grade.quickid.R;
import com.grade.quickid.model.registros.domain.Registro;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Clase que controla el adaptador que maneja los registros
 *
 * @author Cristian Camilo Soto
 */
public class AdapterRegistros extends RecyclerView.Adapter<AdapterRegistros.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private ArrayList<Registro> modelRegistro;
    private LayoutInflater layoutInflater;
    //listener
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public AdapterRegistros(Context context, ArrayList<Registro> modelRegistro) {
        this.layoutInflater = LayoutInflater.from(context);
        this.modelRegistro = modelRegistro;
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onLongClickListener != null) {
            onLongClickListener.onLongClick(v);
            return true;
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_fecha;
        private TextView txt_nombre;
        private TextView txt_lugar;
        private TextView txt_hora;
        private TextView txt_horaNombre;
        private ImageView imageview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_fecha = itemView.findViewById(R.id.txt_fecha);
            txt_lugar = itemView.findViewById(R.id.txt_lugar);
            txt_nombre = itemView.findViewById(R.id.txt_nombre);
            imageview = itemView.findViewById(R.id.idImagen);
            txt_hora = itemView.findViewById(R.id.txt_hora_registro);
            txt_horaNombre = itemView.findViewById(R.id.txt_hora_registro_nombre);
            txt_horaNombre.setVisibility(View.VISIBLE);
            txt_hora.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_list, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRegistros.ViewHolder holder, int position) {
        String nombreActividad = modelRegistro.get(position).getNombreEvento();
        String fechaRegistro = modelRegistro.get(position).getFechaRegistro();
        String lugarActividad = modelRegistro.get(position).getLugarEvento();
        String horaDeRegistro = modelRegistro.get(position).getHoraRegistro();

        holder.txt_nombre.setText(nombreActividad);
        holder.txt_fecha.setText(fechaRegistro);
        holder.txt_lugar.setText(lugarActividad);
        Picasso.get().load(modelRegistro.get(position).getImagenEvento()).fit().centerInside().into(holder.imageview);
        holder.txt_hora.setText(horaDeRegistro);
    }

    @Override
    public int getItemCount() {
        if (modelRegistro != null)
            return modelRegistro.size();
        System.out.println(modelRegistro.size());
        return 0;

    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;

    }
}
