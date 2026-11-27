package com.example.vanessa_mouriz_feed1_gestortareas;

/**
 * Clase del modelo: representa una tarea de la aplicación.
 * Cada objeto Tarea se corresponde con un registro de la tabla "tareas" de la BD.
 */
public class Tarea {

    // Categorías posibles. Cada una tendrá su propio icono en la lista.
    public static final String CATEGORIA_PERSONAL = "Personal";
    public static final String CATEGORIA_TRABAJO = "Trabajo";
    public static final String CATEGORIA_ESTUDIOS = "Estudios";
    public static final String CATEGORIA_CASA = "Casa";

    public static final String[] CATEGORIAS = {
            CATEGORIA_PERSONAL, CATEGORIA_TRABAJO, CATEGORIA_ESTUDIOS, CATEGORIA_CASA
    };

    private long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private boolean completada;

    // Constructor para crear una tarea nueva (todavía sin id, lo asigna la BD)
    public Tarea(String titulo, String descripcion, String categoria, boolean completada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.completada = completada;
    }

    // Constructor para las tareas que recuperamos de la BD (ya tienen id)
    public Tarea(long id, String titulo, String descripcion, String categoria, boolean completada) {
        this(titulo, descripcion, categoria, completada);
        this.id = id;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}
