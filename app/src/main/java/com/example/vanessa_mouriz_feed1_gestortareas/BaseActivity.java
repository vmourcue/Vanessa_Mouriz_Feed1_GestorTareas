package com.example.vanessa_mouriz_feed1_gestortareas;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Clase base de la que heredan todas las Activities de la app.
 * Aquí pongo el código que se repetiría en todas: la Toolbar y el ajuste
 * de márgenes para las barras del sistema.
 */
public abstract class BaseActivity extends AppCompatActivity {

    // Clave del extra con el que pasamos el id de una tarea entre Activities
    public static final String EXTRA_ID_TAREA = "id_tarea";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // La app se dibuja a pantalla completa (por debajo de la barra de estado)
        WindowCompat.enableEdgeToEdge(getWindow());
    }

    /**
     * Configura la Toolbar del layout como ActionBar de la Activity.
     * Hay que llamarlo después de setContentView().
     *
     * @param mostrarFlechaAtras true para mostrar la flecha "Arriba" (navegación ancestral)
     */
    protected void configurarToolbar(boolean mostrarFlechaAtras) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (mostrarFlechaAtras && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // La Toolbar ocupa también la zona de la barra de estado; el resto de la
        // pantalla deja hueco para las barras del sistema y para el teclado.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, 0, barras.right, barras.bottom);
            toolbar.setPadding(0, barras.top, 0, 0);
            return insets;
        });
    }

    // Al pulsar la flecha "Arriba" de la Toolbar cerramos la Activity y volvemos
    // a la anterior. Así no se crea otra MainActivity nueva encima.
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // La opción "Acerca de" está en el menú de todas las pantallas, así que la
    // gestiono aquí. Cada Activity gestiona sus propias opciones y llama a super.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_acerca_de) {
            mostrarAcercaDe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Diálogo "Acerca de" con la versión de la app
    protected void mostrarAcercaDe() {
        String version;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "1.0";
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage(getString(R.string.acerca_de_mensaje, version));
        alertDialog.setPositiveButton(R.string.aceptar, (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    /**
     * Diálogo de confirmación antes de eliminar una tarea.
     * Si el usuario acepta, se ejecuta accionEliminar.
     */
    protected void confirmarEliminar(Tarea tarea, Runnable accionEliminar) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.confirmar_eliminar_titulo);
        alertDialog.setMessage(getString(R.string.confirmar_eliminar_mensaje, tarea.getTitulo()));
        alertDialog.setPositiveButton(R.string.eliminar, (dialog, which) -> accionEliminar.run());
        alertDialog.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}
