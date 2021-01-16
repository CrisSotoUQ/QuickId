package com.grade.quickid.model.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grade.quickid.R;
import com.grade.quickid.model.Registro;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRegistros extends RecyclerView.Adapter<AdapterRegistros.ViewHolder>implements  View.OnClickListener,View.OnLongClickListener {
    ArrayList<Registro> modelRegistro;
    LayoutInflater layoutInflater;
    //listener
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
public AdapterRegistros(Context context, ArrayList<Registro> modelRegistro){
    this.layoutInflater = LayoutInflater.from(context);
    this.modelRegistro = modelRegistro;
}
    @Override
    public void onClick(View v) {
if (onClickListener!= null){
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

    public class ViewHolder extends RecyclerView.ViewHolder{
    TextView txt_fecha,txt_nombre,txt_lugar;
    ImageView imageview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_fecha = itemView.findViewById(R.id.txt_fecha);
            txt_lugar = itemView.findViewById(R.id.txt_lugar);
            txt_nombre = itemView.findViewById(R.id.txt_nombre);
            imageview = itemView.findViewById(R.id.idImagen);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.item_list,parent,false);
    view.setOnClickListener(this);
    view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRegistros.ViewHolder holder, int position) {
        String nombreActividad = modelRegistro.get(position).getNombreActividad();
        String fechaRegistro = modelRegistro.get(position).getFechaRegistro();
        String lugarActividad= modelRegistro.get(position).getLugarActividad();

        holder.txt_nombre.setText(nombreActividad);
        holder.txt_fecha.setText(fechaRegistro);
        holder.txt_lugar.setText(lugarActividad);
        Picasso.get().load(modelRegistro.get(position).getImagenActividad()).fit().centerInside().into(holder.imageview);
    }

    @Override
    public int getItemCount() {
        if (modelRegistro != null)
            return modelRegistro.size();
        System.out.println(modelRegistro.size());
        return 0;

    }
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener){
        this.onLongClickListener = onLongClickListener;
    }
    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;

    }
}
