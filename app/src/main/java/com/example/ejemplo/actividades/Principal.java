package com.example.ejemplo.actividades;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ejemplo.R;
import com.example.ejemplo.adaptadores.UsersAdapter;
import com.example.ejemplo.database.DBHelper;
import com.example.ejemplo.modelos.User;

import java.util.List;

/**
 * `Principal` es una `Activity` (una pantalla) que se muestra después de que el usuario
 * ha iniciado sesión correctamente en `MainActivity`.
 *
 * Su principal objetivo es demostrar cómo:
 * 1. Cargar datos desde una base de datos local (SQLite).
 * 2. Mostrar esos datos en una lista usando un `ListView`.
 * 3. Conectar los datos con la interfaz de usuario a través de un adaptador personalizado (`UsersAdapter`).
 */
public class Principal extends AppCompatActivity {

    // --- MIEMBROS DE LA CLASE (VARIABLES) ---

    /**
     * `listaUsuarios`:
     * Es la referencia al componente `ListView` definido en el archivo de layout `activity_principal.xml`.
     * Un ListView es un widget que muestra una lista de elementos desplazables verticalmente.
     */
    private ListView listaUsuarios;

    /**
     * `dbHelper`:
     * Es una instancia de nuestra clase de ayuda `DBHelper`.
     * Nos proporciona todos los métodos para interactuar con la base de datos (leer, escribir, etc.).
     */
    private DBHelper dbHelper;


    /**
     * --- MÉTODO onCreate ---
     *
     * Este método es llamado por el sistema Android cuando la actividad se crea por primera vez.
     * Aquí es donde debemos realizar toda la configuración inicial:
     * - "Inflar" el layout (cargar la interfaz gráfica).
     * - Inicializar los componentes de la UI.
     * - Cargar los datos que se mostrarán.
     *
     * @param savedInstanceState Si la actividad se está recreando después de haber sido destruida
     *                           (por ejemplo, al rotar la pantalla), este objeto contiene el estado
     *                           guardado previamente. En este caso, no lo usamos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llamada obligatoria al método de la clase padre.

        // --- CONFIGURACIÓN BÁSICA DE LA UI ---

        // `EdgeToEdge.enable(this)` permite que la app se dibuje a pantalla completa, ocupando el espacio
        // detrás de las barras de estado y navegación para un look más moderno.
        EdgeToEdge.enable(this);

        // `setContentView` conecta esta clase de Java con su archivo de layout XML.
        // Le dice a la actividad qué interfaz gráfica debe mostrar.
        setContentView(R.layout.activity_principal);

        // Este listener se encarga de ajustar los paddings (rellenos) de la vista principal (`main`)
        // para que el contenido no quede oculto debajo de las barras del sistema (como la barra de estado).
        // Es parte de la configuración de EdgeToEdge.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // --- INICIALIZACIÓN Y CARGA DE DATOS ---

        // 1. Vinculamos la variable `listaUsuarios` con el componente ListView del XML usando su ID.
        listaUsuarios = findViewById(R.id.lista_usuarios);

        // 2. Creamos una instancia de nuestro `DBHelper`. El `this` se refiere al contexto de esta actividad,
        // que es necesario para que el helper pueda encontrar y gestionar la base de datos.
        dbHelper = new DBHelper(this);

        // 3. Obtenemos la lista de usuarios de la base de datos.
        // Llamamos al método `getAllUsersSafe()`, que nos devuelve una lista de objetos `User`.
        // NOTA IMPORTANTE: Esta operación se está ejecutando en el "hilo principal" (Main Thread).
        // Para listas pequeñas está bien, pero para bases de datos grandes, esto podría bloquear
        // la interfaz de usuario y hacer que la app no responda. En una app real, esto debería
        // hacerse en un hilo secundario (background thread) usando, por ejemplo, AsyncTask, Coroutines o LiveData.
        List<User> users = dbHelper.getAllUsersSafe(); // El método "Safe" no incluye las contraseñas.

        // 4. Creamos una instancia de nuestro adaptador personalizado, `UsersAdapter`.
        // Le pasamos:
        // - El contexto (`Principal.this`).
        // - El layout para cada fila (`R.layout.row_usuario`).
        // - La lista de datos que acabamos de obtener (`users`).
        UsersAdapter adapter = new UsersAdapter(Principal.this, R.layout.row_usuario, users);

        // 5. Asignamos el adaptador a nuestro `ListView`.
        // En este momento, el ListView le pide al adaptador que cree y configure una vista para cada
        // usuario en la lista, y luego las muestra en pantalla.
        listaUsuarios.setAdapter(adapter);

    }
}