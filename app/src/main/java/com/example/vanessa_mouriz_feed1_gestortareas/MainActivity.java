package com.example.vanessa_mouriz_feed1_gestortareas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.ArrayList;

/**
 * Activity principal: muestra todas las tareas guardadas en la BD en un ListView.
 */
public class MainActivity extends BaseActivity {

    // Clave del extra con el que EditarTareaActivity nos devuelve el mensaje
    public static final String EXTRA_RESULTADO = "resultado";

    private ListView listViewTareas;
    private TextView textViewVacio;
    private Button buttonNuevaTarea;
    private TareasDBHelper dbHelper;
    private TareaAdapter adapter;

    // Lanzador para abrir EditarTareaActivity y recibir su resultado
    private ActivityResultLauncher<Intent> editarTareaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configurarToolbar(false);

        listViewTareas = findViewById(R.id.listViewTareas);
        textViewVacio = findViewById(R.id.textViewVacio);
        buttonNuevaTarea = findViewById(R.id.buttonNuevaTarea);

        dbHelper = new TareasDBHelper(this);

        // Registramos el callback que procesará lo que devuelva EditarTareaActivity
        editarTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::procesarResultadoEdicion);

        // Cuando se marca o desmarca el CheckBox, guardamos el cambio en la BD y recargamos
        adapter = new TareaAdapter(this, new ArrayList<>(), (tarea, completada) -> {
            dbHelper.setCompletada(tarea.getId(), completada);
            cargarTareas();
        });

        listViewTareas.setAdapter(adapter);
        listViewTareas.setEmptyView(textViewVacio);

        // Al pulsar una tarea abrimos su detalle (Intent explícito con el id como extra)
        listViewTareas.setOnItemClickListener((parent, view, position, id) -> {
            Tarea tarea = adapter.getItem(position);
            if (tarea != null) {
                Intent intent = new Intent(this, DetalleTareaActivity.class);
                intent.putExtra(EXTRA_ID_TAREA, tarea.getId());
                startActivity(intent);
            }
        });

        buttonNuevaTarea.setOnClickListener(v -> abrirNuevaTarea());

        // Menú contextual flotante al mantener pulsada una tarea
        registerForContextMenu(listViewTareas);
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

    private void abrirEditarTarea(Tarea tarea) {
        Intent intent = new Intent(this, EditarTareaActivity.class);
        intent.putExtra(EXTRA_ID_TAREA, tarea.getId());
        editarTareaLauncher.launch(intent);
    }

    private void eliminarTarea(Tarea tarea) {
        confirmarEliminar(tarea, () -> {
            dbHelper.deleteTarea(tarea.getId());
            Toast.makeText(this, R.string.tarea_eliminada, Toast.LENGTH_SHORT).show();
            cargarTareas();
        });
    }

    private void abrirNuevaTarea() {
        // Sin el extra del id, EditarTareaActivity sabe que es una tarea nueva
        Intent intent = new Intent(this, EditarTareaActivity.class);
        editarTareaLauncher.launch(intent);
    }

    // Callback: se ejecuta al volver de EditarTareaActivity
    private void procesarResultadoEdicion(ActivityResult result) {
        if (result.getResultCode() != Activity.RESULT_OK) {
            return; // El usuario volvió atrás sin guardar
        }
        Intent data = result.getData();
        if (data != null && data.hasExtra(EXTRA_RESULTADO)) {
            Toast.makeText(this, data.getStringExtra(EXTRA_RESULTADO), Toast.LENGTH_SHORT).show();
        }
    }

    // ---------- Menú de opciones ----------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Uso if/else en vez del switch de los apuntes: desde la versión 8 del plugin
    // de Gradle los R.id no son constantes y el switch no compila.
    // "Acerca de" lo gestiona BaseActivity (por eso llamamos a super).
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_nueva_tarea) {
            abrirNuevaTarea();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------- Menú contextual ----------

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual, menu);

        // Ponemos como cabecera del menú el título de la tarea pulsada
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Tarea tarea = adapter.getItem(info.position);
        if (tarea != null) {
            menu.setHeaderTitle(tarea.getTitulo());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // menuInfo nos dice en qué posición de la lista se hizo la pulsación larga
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) {
            return super.onContextItemSelected(item);
        }
        Tarea tarea = adapter.getItem(info.position);
        if (tarea == null) {
            return super.onContextItemSelected(item);
        }

        int id = item.getItemId();
        if (id == R.id.context_editar) {
            abrirEditarTarea(tarea);
            return true;
        } else if (id == R.id.context_eliminar) {
            eliminarTarea(tarea);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
