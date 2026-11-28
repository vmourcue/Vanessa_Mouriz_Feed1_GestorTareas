package com.example.vanessa_mouriz_feed1_gestortareas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Arrays;

/**
 * Activity para crear una tarea nueva o editar una existente.
 * Si recibe el extra EXTRA_ID_TAREA, edita esa tarea; si no, crea una nueva.
 */
public class EditarTareaActivity extends BaseActivity {

    private EditText editTextTitulo;
    private EditText editTextDescripcion;
    private Spinner spinnerCategoria;
    private ImageView imageViewIcono;
    private CheckBox checkBoxCompletada;
    private Button buttonGuardar;

    private TareasDBHelper dbHelper;

    // Tarea que estamos editando, o null si es una tarea nueva
    private Tarea tareaEditada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_tarea);
        configurarToolbar(true);

        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        imageViewIcono = findViewById(R.id.imageViewIcono);
        checkBoxCompletada = findViewById(R.id.checkBoxCompletada);
        buttonGuardar = findViewById(R.id.buttonGuardar);

        dbHelper = new TareasDBHelper(this);

        configurarSpinner();

        // Recuperamos el id del Intent. Si no viene, el valor por defecto es -1
        long idTarea = getIntent().getLongExtra(EXTRA_ID_TAREA, -1);
        if (idTarea != -1) {
            tareaEditada = dbHelper.getTarea(idTarea);
        }

        if (tareaEditada != null) {
            setTitle(R.string.titulo_editar_tarea);
            rellenarCampos(tareaEditada);
        } else {
            setTitle(R.string.titulo_nueva_tarea);
        }

        buttonGuardar.setOnClickListener(v -> guardarTarea());
    }

    private void configurarSpinner() {
        // Adaptador sencillo con los layouts que trae Android para los Spinner
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Tarea.CATEGORIAS);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategorias);

        // Cada vez que se elige una categoría, cambiamos el icono de al lado
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Tarea tareaTemporal = new Tarea("", "", Tarea.CATEGORIAS[position], false);
                imageViewIcono.setImageResource(tareaTemporal.getIconoResId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hace falta hacer nada
            }
        });
    }

    private void rellenarCampos(Tarea tarea) {
        editTextTitulo.setText(tarea.getTitulo());
        editTextDescripcion.setText(tarea.getDescripcion());
        checkBoxCompletada.setChecked(tarea.isCompletada());

        int posicion = Arrays.asList(Tarea.CATEGORIAS).indexOf(tarea.getCategoria());
        if (posicion >= 0) {
            spinnerCategoria.setSelection(posicion);
        }
    }

    private void guardarTarea() {
        String titulo = editTextTitulo.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String categoria = (String) spinnerCategoria.getSelectedItem();
        boolean completada = checkBoxCompletada.isChecked();

        // El título es obligatorio
        if (titulo.isEmpty()) {
            editTextTitulo.setError(getString(R.string.error_titulo_vacio));
            editTextTitulo.requestFocus();
            return;
        }

        String mensaje;
        if (tareaEditada == null) {
            dbHelper.addTarea(new Tarea(titulo, descripcion, categoria, completada));
            mensaje = getString(R.string.tarea_creada);
        } else {
            tareaEditada.setTitulo(titulo);
            tareaEditada.setDescripcion(descripcion);
            tareaEditada.setCategoria(categoria);
            tareaEditada.setCompletada(completada);
            dbHelper.updateTarea(tareaEditada);
            mensaje = getString(R.string.tarea_actualizada);
        }

        // Devolvemos el resultado a la Activity que nos abrió
        Intent replyIntent = new Intent();
        replyIntent.putExtra(MainActivity.EXTRA_RESULTADO, mensaje);
        setResult(Activity.RESULT_OK, replyIntent);
        finish();
    }

    // ---------- Menú de opciones ----------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_guardar) {
            guardarTarea();
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
