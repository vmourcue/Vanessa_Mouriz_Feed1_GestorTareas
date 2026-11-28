package com.example.vanessa_mouriz_feed1_gestortareas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Segundo adaptador, para la lista de tareas completadas.
 * A diferencia de TareaAdapter, este hereda de BaseAdapter, así que hay que
 * implementar a mano getCount(), getItem() y getItemId().
 * Usa otro layout: item_tarea_completada.xml.
 */
public class TareaCompletadaAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private List<Tarea> tareas = new ArrayList<>();

    public TareaCompletadaAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    // Cambia los datos de la lista y avisa al ListView para que se repinte
    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tareas.size();
    }

    @Override
    public Tarea getItem(int position) {
        return tareas.get(position);
    }

    // El id de cada fila es el id de la tarea en la BD
    @Override
    public long getItemId(int position) {
        return tareas.get(position).getId();
    }

    static class ViewHolder {
        ImageView imageViewIcono;
        TextView textViewTitulo;
        TextView textViewDescripcion;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_tarea_completada, parent, false);
            holder = new ViewHolder();
            holder.imageViewIcono = convertView.findViewById(R.id.imageViewIcono);
            holder.textViewTitulo = convertView.findViewById(R.id.textViewTitulo);
            holder.textViewDescripcion = convertView.findViewById(R.id.textViewDescripcion);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tarea tarea = getItem(position);
        holder.imageViewIcono.setImageResource(tarea.getIconoResId());
        holder.textViewTitulo.setText(tarea.getTitulo());

        if (tarea.getDescripcion() == null || tarea.getDescripcion().isEmpty()) {
            holder.textViewDescripcion.setText(context.getString(R.string.sin_descripcion));
        } else {
            holder.textViewDescripcion.setText(tarea.getDescripcion());
        }

        return convertView;
    }
}
