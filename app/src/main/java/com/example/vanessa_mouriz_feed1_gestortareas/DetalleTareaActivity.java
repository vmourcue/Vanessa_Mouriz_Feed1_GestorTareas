package com.example.vanessa_mouriz_feed1_gestortareas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

/**
 * Activity que muestra el detalle de una tarea.
 * Desde aquí se puede marcar como completada, editarla o eliminarla.
 */
public class DetalleTareaActivity extends BaseActivity {

    private ImageView imageViewIcono;
    private TextView textViewTitulo;
    private TextView textViewCategoria;
    private TextView textViewDescripcion;
    private CheckBox checkBoxCompletada;
    private Button buttonEditar;
    private Button buttonEliminar;

    private TareasDBHelper dbHelper;
    private long idTarea;
    private Tarea tarea;

    private ActivityResultLauncher<Intent> editarTareaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_tarea);
        configurarToolbar(true);
        setTitle(R.string.titulo_detalle_tarea);

        imageViewIcono = findViewById(R.id.imageViewIcono);
        textViewTitulo = findViewById(R.id.textViewTitulo);
        textViewCategoria = findViewById(R.id.textViewCategoria);
        textViewDescripcion = findViewById(R.id.textViewDescripcion);
        checkBoxCompletada = findViewById(R.id.checkBoxCompletada);
        buttonEditar = findViewById(R.id.buttonEditar);
        buttonEliminar = findViewById(R.id.buttonEliminar);

        dbHelper = new TareasDBHelper(this);

        // Recuperamos el id de la tarea que nos pasa MainActivity
        idTarea = getIntent().getLongExtra(EXTRA_ID_TAREA, -1);

        editarTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::procesarResultadoEdicion);

        // Al marcar el CheckBox guardamos el estado en la BD
        checkBoxCompletada.setOnClickListener(v -> {
            dbHelper.setCompletada(idTarea, checkBoxCompletada.isChecked());
            mostrarTarea();
        });

        buttonEditar.setOnClickListener(v -> editarTarea());
        buttonEliminar.setOnClickListener(v -> eliminarTarea());
    }

    // Cargamos la tarea en onResume para que, al volver de editarla, se vea actualizada
    @Override
    protected void onResume() {
        super.onResume();
        mostrarTarea();
    }

    private void mostrarTarea() {
        tarea = dbHelper.getTarea(idTarea);
        if (tarea == null) {
            Toast.makeText(this, R.string.tarea_no_encontrada, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageViewIcono.setImageResource(tarea.getIconoResId());
        textViewTitulo.setText(tarea.getTitulo());
        textViewCategoria.setText(tarea.getCategoria());
        checkBoxCompletada.setChecked(tarea.isCompletada());

        if (tarea.getDescripcion() == null || tarea.getDescripcion().isEmpty()) {
            textViewDescripcion.setText(R.string.sin_descripcion);
        } else {
            textViewDescripcion.setText(tarea.getDescripcion());
        }
    }

    private void editarTarea() {
        Intent intent = new Intent(this, EditarTareaActivity.class);
        intent.putExtra(EXTRA_ID_TAREA, idTarea);
        editarTareaLauncher.launch(intent);
    }

    private void procesarResultadoEdicion(ActivityResult result) {
        if (result.getResultCode() != Activity.RESULT_OK) {
            return;
        }
        Intent data = result.getData();
        if (data != null && data.hasExtra(EXTRA_RESULTADO)) {
            Toast.makeText(this, data.getStringExtra(EXTRA_RESULTADO),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Pide confirmación con un diálogo (está en BaseActivity) y, si se acepta, borra la tarea
    private void eliminarTarea() {
        confirmarEliminar(tarea, () -> {
            dbHelper.deleteTarea(idTarea);
            Toast.makeText(this, R.string.tarea_eliminada, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // ---------- Menú de opciones ----------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    // Uso if/else en vez del switch de los apuntes: desde la versión 8 del plugin
    // de Gradle los R.id no son constantes y el switch no compila.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_editar) {
            editarTarea();
            return true;
        } else if (id == R.id.action_eliminar) {
            eliminarTarea();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
