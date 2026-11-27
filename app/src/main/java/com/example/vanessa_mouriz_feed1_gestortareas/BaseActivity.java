package com.example.vanessa_mouriz_feed1_gestortareas;

import android.os.Bundle;

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
}
