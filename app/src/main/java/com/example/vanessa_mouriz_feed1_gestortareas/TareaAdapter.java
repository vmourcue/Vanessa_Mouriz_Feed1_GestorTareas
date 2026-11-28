package com.example.vanessa_mouriz_feed1_gestortareas;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Adaptador propio para mostrar las tareas en un ListView.
 * Cada fila usa el layout item_tarea.xml (icono, título, categoría y CheckBox).
 */
public class TareaAdapter extends ArrayAdapter<Tarea> {

    // Interfaz para avisar a la Activity cuando el usuario marca o desmarca una tarea
    public interface OnCompletadaChangeListener {
        void onCompletadaChange(Tarea tarea, boolean completada);
    }

    private final LayoutInflater inflater;
    private final OnCompletadaChangeListener listener;

    public TareaAdapter(Context context, List<Tarea> tareas, OnCompletadaChangeListener listener) {
        super(context, 0, tareas);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    // ViewHolder: guarda las vistas de la fila para no repetir findViewById cada vez
    static class ViewHolder {
        ImageView imageViewIcono;
        TextView textViewTitulo;
        TextView textViewCategoria;
        CheckBox checkBoxCompletada;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        // Si no hay una fila que reutilizar, inflamos una nueva
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_tarea, parent, false);
            holder = new ViewHolder();
            holder.imageViewIcono = convertView.findViewById(R.id.imageViewIcono);
            holder.textViewTitulo = convertView.findViewById(R.id.textViewTitulo);
            holder.textViewCategoria = convertView.findViewById(R.id.textViewCategoria);
            holder.checkBoxCompletada = convertView.findViewById(R.id.checkBoxCompletada);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tarea tarea = getItem(position);
        if (tarea == null) {
            return convertView;
        }

        holder.imageViewIcono.setImageResource(tarea.getIconoResId());
        holder.textViewCategoria.setText(tarea.getCategoria());

        // Si la tarea está completada, tachamos el título con un StrikethroughSpan
        // y además lo atenuamos, para que se distinga también cuando la pantalla
        // es pequeña y la línea del tachado apenas se ve.
        if (tarea.isCompletada()) {
            SpannableString tituloTachado = new SpannableString(tarea.getTitulo());
            tituloTachado.setSpan(new StrikethroughSpan(), 0, tituloTachado.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.textViewTitulo.setText(tituloTachado);
            holder.textViewTitulo.setAlpha(0.6f);
        } else {
            holder.textViewTitulo.setText(tarea.getTitulo());
            holder.textViewTitulo.setAlpha(1f);
        }

        // Quito el listener antes de llamar a setChecked; si no, al reutilizar
        // la fila saltaría el evento de la tarea anterior.
        holder.checkBoxCompletada.setOnCheckedChangeListener(null);
        holder.checkBoxCompletada.setChecked(tarea.isCompletada());
        holder.checkBoxCompletada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCompletadaChange(tarea, isChecked);
            }
        });

        return convertView;
    }
}
