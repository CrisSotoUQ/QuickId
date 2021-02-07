package com.grade.quickid.model.actividades.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grade.quickid.R;
import com.grade.quickid.model.actividades.Actividad;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterActividades extends RecyclerView.Adapter<AdapterActividades.ViewHolder> implements  View.OnLongClickListener,View.OnClickListener {
    ArrayList<Actividad> modelActividad;
    LayoutInflater layoutInflater;
    //listener
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
        Context context;
    public AdapterActividades(Context context, ArrayList<Actividad> modelActividad){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.modelActividad = modelActividad;
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
        public TextView txt_fecha,txt_nombre,txt_lugar;
        public ImageView imageview;
        private View item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_fecha = itemView.findViewById(R.id.txt_fecha);
            txt_lugar = itemView.findViewById(R.id.txt_lugar);
            txt_nombre = itemView.findViewById(R.id.txt_nombre);
            imageview = itemView.findViewById(R.id.idImagen);
            this.item = itemView;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_list,parent,false);
        view.setOnLongClickListener(this);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nombreActividad = modelActividad.get(position).getNombre();
        String fechaActividad = modelActividad.get(position).getfIni();
        String lugarActividad= modelActividad.get(position).getLugar();
        holder.txt_nombre.setText(nombreActividad);
        holder.txt_fecha.setText((CharSequence) fechaActividad);
        holder.txt_lugar.setText(lugarActividad);
        Picasso.get().load(modelActividad.get(position).getUrlImagen()).fit().centerInside().into(holder.imageview);
    }

    @Override
    public int getItemCount() {
        if (modelActividad != null)
            return modelActividad.size();
        System.out.println(modelActividad.size());
        return 0;

    }
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener){
        this.onLongClickListener = onLongClickListener;

    }
    public  void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener=onClickListener;

    }
}
