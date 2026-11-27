package com.example.vanessa_mouriz_feed1_gestortareas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

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
        buttonEliminar.setOnClickListener(v -> confirmarEliminar());
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
        if (data != null && data.hasExtra(MainActivity.EXTRA_RESULTADO)) {
            Toast.makeText(this, data.getStringExtra(MainActivity.EXTRA_RESULTADO),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Diálogo de confirmación antes de borrar (AlertDialog con botón positivo y negativo)
    private void confirmarEliminar() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.confirmar_eliminar_titulo);
        alertDialog.setMessage(getString(R.string.confirmar_eliminar_mensaje, tarea.getTitulo()));
        alertDialog.setPositiveButton(R.string.eliminar, (dialog, which) -> {
            dbHelper.deleteTarea(idTarea);
            Toast.makeText(this, R.string.tarea_eliminada, Toast.LENGTH_SHORT).show();
            finish();
        });
        alertDialog.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
