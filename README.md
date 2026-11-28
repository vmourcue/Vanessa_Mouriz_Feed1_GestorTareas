# Gestor de Tareas

Aplicación Android para gestionar tareas personales, desarrollada para el Ejercicio de feedback 1 de la asignatura *Programación dirigida por eventos* (Grado en Ingeniería Informática, UAX).

**Autora:** Vanessa Mouriz

## Descarga

- **Descarga directa del APK:** [Vanessa_Mouriz_GestorTareas.apk](https://github.com/vmourcue/Vanessa_Mouriz_Feed1_GestorTareas/releases/latest/download/Vanessa_Mouriz_GestorTareas.apk)
- Todas las versiones: [Releases](https://github.com/vmourcue/Vanessa_Mouriz_Feed1_GestorTareas/releases)

Para instalarlo en el móvil hay que permitir la instalación de aplicaciones de origen desconocido. La app funciona desde Android 7.0.

## Requisitos técnicos

- Lenguaje: Java
- SDK mínimo: API 24 (Android 7.0)
- Base de datos: SQLite (mediante `SQLiteOpenHelper`)
- IDE: Android Studio

## Funcionalidades

- **Lista de tareas** (pantalla principal) en un `ListView` con un adaptador propio (`TareaAdapter`, que hereda de `ArrayAdapter`). Cada fila muestra el icono de la categoría, el título, la categoría y un `CheckBox` para marcar la tarea como completada. Las completadas aparecen tachadas y al final de la lista.
- **Crear y editar tareas** con título, descripción, categoría (`Spinner`) y estado. El icono cambia según la categoría elegida.
- **Detalle de una tarea** con su icono, título, categoría, descripción y estado, y botones para editarla o eliminarla.
- **Eliminar tareas** desde el detalle o desde el menú contextual de la lista, siempre con un diálogo de confirmación.
- **Tareas completadas**: pantalla aparte con otro `ListView`, otro adaptador (`TareaCompletadaAdapter`, que hereda de `BaseAdapter`) y otro layout. Desde aquí se pueden borrar todas las completadas a la vez.
- **Imágenes como atributo**: cada tarea tiene una categoría (Personal, Trabajo, Estudios o Familia) y cada categoría tiene su propio icono.
- **Menú de opciones en la barra de aplicaciones** en todas las pantallas, con las acciones de cada una y la opción **"Acerca de"**, que muestra un diálogo con la información de la app.
- **Persistencia**: todo se guarda en una base de datos SQLite, así que las tareas se conservan al cerrar la app. La primera vez se crean unas tareas de ejemplo.

## Estructura del proyecto

| Clase | Descripción |
| --- | --- |
| `Tarea` | Clase del modelo (una fila de la tabla `tareas`) |
| `TareasDBHelper` | Acceso a la base de datos: crear tabla, insertar, modificar, borrar y consultar |
| `BaseActivity` | Clase base de las Activities: Toolbar, diálogo "Acerca de" y diálogo de confirmación |
| `MainActivity` | Lista de todas las tareas |
| `DetalleTareaActivity` | Detalle de una tarea |
| `EditarTareaActivity` | Alta y edición de tareas |
| `CompletadasActivity` | Lista de tareas completadas |
| `TareaAdapter` | Adaptador de la lista principal (`ArrayAdapter`) |
| `TareaCompletadaAdapter` | Adaptador de la lista de completadas (`BaseAdapter`) |

## Modo de uso

1. Al abrir la app aparece la lista de tareas.
2. Para **crear una tarea**, pulsa el botón **"Nueva tarea"** o el icono **+** de la barra superior. Rellena el título (obligatorio), la descripción y la categoría, y pulsa **Guardar**.
3. Para **marcar una tarea como completada**, marca su casilla en la lista o en su pantalla de detalle.
4. Para **ver el detalle** de una tarea, púlsala en la lista. Desde el detalle puedes **editarla** o **eliminarla** con los botones o con los iconos de la barra superior.
5. **Mantén pulsada** una tarea de la lista para abrir el menú contextual con las opciones **Editar** y **Eliminar**.
6. En el menú de los tres puntos de la pantalla principal, **"Ver completadas"** abre la lista de tareas terminadas. Desde ahí puedes borrarlas todas con el icono de la papelera.
7. **"Acerca de"**, en el menú de los tres puntos, muestra la información de la aplicación.
8. La flecha de la barra superior vuelve a la pantalla anterior.

## Posibles mejoras

- Hacer las operaciones de base de datos en un hilo secundario (con `ExecutorService`) para no bloquear la interfaz si hubiera muchas tareas, o migrar a Room con `ViewModel` y `LiveData`.
- Añadir una fecha límite a las tareas, con un `DatePickerDialog`, y ordenar por fecha.
- Recordatorios con notificaciones o alarmas antes de la fecha límite.
- Buscar tareas por texto y filtrar por categoría.
- Permitir que el usuario asigne una foto de la galería a una tarea.
- Sustituir el `ListView` por un `RecyclerView`.
- Deshacer el borrado de una tarea con un `Snackbar`.
