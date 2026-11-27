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
    private static final int DATABASE_VERSION = 1;

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
