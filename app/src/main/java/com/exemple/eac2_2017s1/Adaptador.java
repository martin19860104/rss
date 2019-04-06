package com.exemple.eac2_2017s1;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ElMeuViewHolder> {
    private List<XmlParser.Entrada> lista;
    private Context context;
    public Adaptador(Context context) {
        this.context = context;
    }
    @Override
    public Adaptador.ElMeuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila, null);
        ElMeuViewHolder viewHolder = new ElMeuViewHolder(itemLayoutView);
        return viewHolder;
    }

    //Returns the amount of data
    @Override
    public int getItemCount() {
        if(lista == null) return 0;
        else return lista.size();
    }
    //load the widgets with the data (it is invoked by the layout manager)
    @Override
    public void onBindViewHolder(ElMeuViewHolder viewHolder, int position) {
        /* *
         * position contains the position of the current item in the list. will also use it as an index to retrieve the data
         * */
//        String url = lista.get(position).imagen;
//        Log.e("@@@@@@@@@@@@",url);
//        Log.e("@@@@@@@@@@@@",""+lista.get(position));
//        String fileName = url.substring(url.lastIndexOf('/')+1, url.length() );
//        String path = context.getCacheDir().toString()+ File.separator+fileName;
//        Drawable drawable = Drawable.createFromPath(path);
//        if (drawable != null){
//            viewHolder.imageView.setImageDrawable(drawable);
//        }
            Log.e("#############", lista.get(position).titulo);
        viewHolder.vTitle.setText(lista.get(position).titulo);
        viewHolder.vtime.setText(lista.get(position).fecha);
    }

    public void setList(List lista) {
        this.lista = lista;
    }

    public List<XmlParser.Entrada> getList(){
        return lista;
    }
    public class ElMeuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
//        protected ImageView imageView;
        protected TextView vTitle;
        protected TextView vtime;
        public ElMeuViewHolder(View v) {
            super(v);
//            imageView = (ImageView) v.findViewById(R.id.imageView);
            vTitle = (TextView) v.findViewById(R.id.title);
            vtime = (TextView) v.findViewById(R.id.time);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("@@@@@@@@@@@@", ""+lista.get(getAdapterPosition()));
            Intent intent = new Intent(context, WebVisor.class);
            intent.putExtra("item", lista.get(getAdapterPosition()));

            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            int posicion = getAdapterPosition();
            lista.remove(posicion);
            notifyItemRemoved(posicion);
            return true;
        }

    }

}