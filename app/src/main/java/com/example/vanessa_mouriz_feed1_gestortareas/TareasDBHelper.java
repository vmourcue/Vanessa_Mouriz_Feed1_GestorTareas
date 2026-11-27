package com.example.vanessa_mouriz_feed1_gestortareas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a la base de datos SQLite.
 * Hereda de SQLiteOpenHelper, que se encarga de crear la BD y gestionar su versión.
 */
public class TareasDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tareas.db";
    // Versión 2: cambio la categoría "Casa" por "Familia" y las tareas de ejemplo.
    // Al subir el número, Android llama a onUpgrade() y la tabla se vuelve a crear.
    private static final int DATABASE_VERSION = 2;

    // Tabla y columnas
    public static final String TABLE_TAREAS = "tareas";
    public static final String COL_ID = "id";
    public static final String COL_TITULO = "titulo";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_CATEGORIA = "categoria";
    public static final String COL_COMPLETADA = "completada"; // 0 = pendiente, 1 = completada

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_TAREAS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TITULO + " TEXT NOT NULL, " +
                    COL_DESCRIPCION + " TEXT, " +
                    COL_CATEGORIA + " TEXT, " +
                    COL_COMPLETADA + " INTEGER DEFAULT 0)";

    private static final String[] TODAS_LAS_COLUMNAS = {
            COL_ID, COL_TITULO, COL_DESCRIPCION, COL_CATEGORIA, COL_COMPLETADA
    };

    public TareasDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

        // Inserto unas tareas de ejemplo para que la lista no aparezca vacía la primera vez
        insertarEjemplo(db, "Estudiar la unidad 3 de Programación dirigida por eventos",
                "Tareas en segundo plano, notificaciones y alarmas", Tarea.CATEGORIA_ESTUDIOS, false);
        insertarEjemplo(db, "Entregar el ejercicio de feedback 1",
                "Subir el código al repositorio y generar el APK", Tarea.CATEGORIA_ESTUDIOS, false);
        insertarEjemplo(db, "Preparar el informe de estado del proyecto",
                "Avance, riesgos y próximos hitos para el comité de seguimiento", Tarea.CATEGORIA_TRABAJO, false);
        insertarEjemplo(db, "Revisar los SLA de las incidencias abiertas",
                "Comprobar las incidencias cerca de incumplir el SLA y escalar si hace falta", Tarea.CATEGORIA_TRABAJO, false);
        insertarEjemplo(db, "Llevar al niño a extraescolares",
                "Lunes, miércoles y viernes a las 18:00", Tarea.CATEGORIA_FAMILIA, false);
        insertarEjemplo(db, "Reservar pista para el partido con mi hijo",
                "Sábado a las 10:00", Tarea.CATEGORIA_FAMILIA, false);
        insertarEjemplo(db, "Ir a la clase de tenis",
                "Lunes, miércoles y jueves a las 20:00", Tarea.CATEGORIA_PERSONAL, false);
        insertarEjemplo(db, "Pedir cita con el oftalmólogo para el niño",
                "", Tarea.CATEGORIA_FAMILIA, true);
        insertarEjemplo(db, "Pedir cita con el dermatólogo",
                "", Tarea.CATEGORIA_PERSONAL, true);
    }

    // Aquí uso el db que recibe onCreate. No puedo llamar a addTarea() porque
    // volvería a pedir getWritableDatabase() mientras la BD se está creando.
    private void insertarEjemplo(SQLiteDatabase db, String titulo, String descripcion,
                                 String categoria, boolean completada) {
        db.insert(TABLE_TAREAS, null,
                tareaToContentValues(new Tarea(titulo, descripcion, categoria, completada)));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        onCreate(db);
    }

    // ---------- Inserción ----------

    // Inserta una tarea y devuelve el id que le ha asignado la BD
    public long addTarea(Tarea tarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(TABLE_TAREAS, null, tareaToContentValues(tarea));
        db.close();
        return id;
    }

    // ---------- Actualización ----------

    public int updateTarea(Tarea tarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.update(
                TABLE_TAREAS,
                tareaToContentValues(tarea),
                COL_ID + " = ?",
                new String[]{String.valueOf(tarea.getId())}
        );
        db.close();
        return filas;
    }

    // Marca o desmarca una tarea como completada (lo usaremos desde el CheckBox de la lista)
    public int setCompletada(long id, boolean completada) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMPLETADA, completada ? 1 : 0);
        int filas = db.update(
                TABLE_TAREAS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return filas;
    }

    // ---------- Borrado ----------

    public int deleteTarea(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete(
                TABLE_TAREAS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return filas;
    }

    // ---------- Consultas ----------

    // Devuelve una tarea por su id, o null si no existe
    public Tarea getTarea(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TAREAS,
                TODAS_LAS_COLUMNAS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
        Tarea tarea = null;
        if (cursor.moveToFirst()) {
            tarea = cursorToTarea(cursor);
        }
        cursor.close();
        return tarea;
    }

    // Devuelve todas las tareas: primero las pendientes y después por título
    public List<Tarea> getTodasTareas() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TAREAS,
                TODAS_LAS_COLUMNAS,
                null,
                null,
                null,
                null,
                COL_COMPLETADA + " ASC, " + COL_TITULO + " ASC"
        );
        return cursorToLista(cursor);
    }

    // Devuelve solo las tareas completadas o solo las pendientes
    public List<Tarea> getTareasPorEstado(boolean completadas) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TAREAS,
                TODAS_LAS_COLUMNAS,
                COL_COMPLETADA + " = ?",
                new String[]{completadas ? "1" : "0"},
                null,
                null,
                COL_TITULO + " ASC"
        );
        return cursorToLista(cursor);
    }

    // ---------- Métodos auxiliares ----------

    private ContentValues tareaToContentValues(Tarea tarea) {
        ContentValues values = new ContentValues();
        values.put(COL_TITULO, tarea.getTitulo());
        values.put(COL_DESCRIPCION, tarea.getDescripcion());
        values.put(COL_CATEGORIA, tarea.getCategoria());
        values.put(COL_COMPLETADA, tarea.isCompletada() ? 1 : 0);
        return values;
    }

    private Tarea cursorToTarea(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        String titulo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITULO));
        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION));
        String categoria = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORIA));
        boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COMPLETADA)) == 1;
        return new Tarea(id, titulo, descripcion, categoria, completada);
    }

    private List<Tarea> cursorToLista(Cursor cursor) {
        List<Tarea> lista = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToTarea(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
