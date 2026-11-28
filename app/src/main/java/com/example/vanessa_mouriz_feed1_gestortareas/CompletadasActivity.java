package com.example.vanessa_mouriz_feed1_gestortareas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/**
 * Activity que muestra solo las tareas completadas, con un adaptador y un layout
 * distintos a los de la pantalla principal.
 */
public class CompletadasActivity extends BaseActivity {

    private ListView listViewCompletadas;
    private TextView textViewVacio;
    private TareasDBHelper dbHelper;
    private TareaCompletadaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completadas);
        configurarToolbar(true);
        setTitle(R.string.titulo_completadas);

        listViewCompletadas = findViewById(R.id.listViewCompletadas);
        textViewVacio = findViewById(R.id.textViewVacio);

        dbHelper = new TareasDBHelper(this);

        adapter = new TareaCompletadaAdapter(this);
        listViewCompletadas.setAdapter(adapter);
        listViewCompletadas.setEmptyView(textViewVacio);

        // Con BaseAdapter, el parámetro id del listener es el que devuelve getItemId(),
        // es decir, el id de la tarea en la BD
        listViewCompletadas.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, DetalleTareaActivity.class);
            intent.putExtra(EXTRA_ID_TAREA, id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCompletadas();
    }

    private void cargarCompletadas() {
        adapter.setTareas(dbHelper.getTareasPorEstado(true));
    }

    private void eliminarCompletadas() {
        if (adapter.getCount() == 0) {
            Toast.makeText(this, R.string.lista_completadas_vacia, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.eliminar_completadas);
        alertDialog.setMessage(R.string.confirmar_eliminar_completadas);
        alertDialog.setPositiveButton(R.string.eliminar, (dialog, which) -> {
            int borradas = dbHelper.deleteCompletadas();
            Toast.makeText(this, getString(R.string.completadas_eliminadas, borradas),
                    Toast.LENGTH_SHORT).show();
            cargarCompletadas();
        });
        alertDialog.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    // ---------- Menú de opciones ----------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completadas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_eliminar_completadas) {
            eliminarCompletadas();
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
