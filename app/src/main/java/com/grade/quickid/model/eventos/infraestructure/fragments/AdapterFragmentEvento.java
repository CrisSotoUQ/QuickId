package com.grade.quickid.model.eventos.infraestructure.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grade.quickid.R;
import com.grade.quickid.model.eventos.domain.Evento;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterFragmentEvento extends RecyclerView.Adapter<AdapterFragmentEvento.ViewHolder> implements  View.OnLongClickListener,View.OnClickListener {
    private ArrayList<Evento> modelEvento;
    private LayoutInflater layoutInflater;
    //listener
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private  Context context;
    public AdapterFragmentEvento(Context context, ArrayList<Evento> modelEvento){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.modelEvento = modelEvento;
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
        String nombreActividad = modelEvento.get(position).getNombre();
        String fechaActividad = modelEvento.get(position).getfIni();
        String lugarActividad= modelEvento.get(position).getLugar();
        holder.txt_nombre.setText(nombreActividad);
        holder.txt_fecha.setText((CharSequence) fechaActividad);
        holder.txt_lugar.setText(lugarActividad);
        Picasso.get().load(modelEvento.get(position).getUrlImagen()).fit().centerInside().into(holder.imageview);
    }

    @Override
    public int getItemCount() {
        if (modelEvento != null)
            return modelEvento.size();
        System.out.println(modelEvento.size());
        return 0;

    }
   public void setOnLongClickListener(View.OnLongClickListener onLongClickListener){
        this.onLongClickListener = onLongClickListener;
        

    }
    public  void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener=onClickListener;

    }
}
