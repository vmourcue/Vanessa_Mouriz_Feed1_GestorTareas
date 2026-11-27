package com.example.vanessa_mouriz_feed1_gestortareas;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

/**
 * Activity principal: muestra todas las tareas guardadas en la BD en un ListView.
 */
public class MainActivity extends AppCompatActivity {

    private ListView listViewTareas;
    private TextView textViewVacio;
    private TareasDBHelper dbHelper;
    private TareaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        // Usamos nuestra Toolbar como ActionBar de la Activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // La Toolbar ocupa también la zona de la barra de estado;
        // el resto de la pantalla deja hueco para las barras del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            toolbar.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        listViewTareas = findViewById(R.id.listViewTareas);
        textViewVacio = findViewById(R.id.textViewVacio);

        dbHelper = new TareasDBHelper(this);

        // Cuando se marca o desmarca el CheckBox, guardamos el cambio en la BD y recargamos
        adapter = new TareaAdapter(this, new ArrayList<>(), (tarea, completada) -> {
            dbHelper.setCompletada(tarea.getId(), completada);
            cargarTareas();
        });

        listViewTareas.setAdapter(adapter);
        listViewTareas.setEmptyView(textViewVacio);

        // De momento, al pulsar una tarea solo mostramos su título.
        // Más adelante abrirá la pantalla de detalle.
        listViewTareas.setOnItemClickListener((parent, view, position, id) -> {
            Tarea tarea = adapter.getItem(position);
            if (tarea != null) {
                Toast.makeText(this, tarea.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // onResume se ejecuta cada vez que la Activity vuelve a primer plano,
    // así la lista siempre está actualizada al volver de otra pantalla.
    @Override
    protected void onResume() {
        super.onResume();
        cargarTareas();
    }

    private void cargarTareas() {
        adapter.clear();
        adapter.addAll(dbHelper.getTodasTareas());
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
