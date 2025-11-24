package com.example.ejemplo.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ejemplo.R;
import com.example.ejemplo.database.DBHelper;
import com.example.ejemplo.modelos.User;
/**
 ███╗   ███╗     █████╗     ██╗    ███╗   ██╗
 ████╗ ████║    ██╔══██╗    ██║    ████╗  ██║
 ██╔████╔██║    ███████║    ██║    ██╔██╗ ██║
 ██║╚██╔╝██║    ██╔══██║    ██║    ██║╚██╗██║
 ██║ ╚═╝ ██║    ██║  ██║    ██║    ██║ ╚████║
 ╚═╝     ╚═╝    ╚═╝  ╚═╝    ╚═╝    ╚═╝  ╚═══╝

 █████╗      ██████╗    ████████╗    ██╗    ██╗   ██╗    ██╗    ████████╗    ██╗   ██╗
 ██╔══██╗    ██╔════╝    ╚══██╔══╝    ██║    ██║   ██║    ██║    ╚══██╔══╝    ╚██╗ ██╔╝
 ███████║    ██║            ██║       ██║    ██║   ██║    ██║       ██║        ╚████╔╝
 ██╔══██║    ██║            ██║       ██║    ╚██╗ ██╔╝    ██║       ██║         ╚██╔╝
 ██║  ██║    ╚██████╗       ██║       ██║     ╚████╔╝     ██║       ██║          ██║
 ╚═╝  ╚═╝     ╚═════╝       ╚═╝       ╚═╝      ╚═══╝      ╚═╝       ╚═╝          ╚═╝
 * MainActivity:
 * - Muestra campos de usuario y contraseña.
 * - Intenta loguear localmente consultando DBHelper.
 * - Si el usuario existe lanza la actividad Principal.
 *
 * Notas importantes:
 * - En onCreate se inserta un usuario "admin"/"admin". Esto está bien para pruebas,
 *   pero en producción deberías evitar insertar repetidamente el mismo usuario
 *   (podría crear duplicados) o envolverlo en una verificación previa.
 * - Las contraseñas se manejan en texto plano en este ejemplo. En producción usa
 *   hashing+salt y nunca almacenes passwords en claro.
 */
public class MainActivity extends AppCompatActivity {

    // Vistas del layout
    EditText input_usuario, input_contrasena;
    Button btn_ingresar;

    // Variables para mantener texto ingresado
    String nombreUsuario, contraseña;

    // Helper para DB (clase que ya tenés con los métodos CRUD)
    DBHelper dbHelper;


    /**
     *
     * La funcion onCreate es un metodo propio de una pantalla que nos muesta el codigo que
     * se ejecuta una vez que el usuario la activa o la crea por primera vez.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******************************************************
         *                                                    *
         *   Esta seccion por lo general viene cargada por    *
         *   defecto y no se suele tocar a menos que quieras  *
         *   personalizar los bordes de la app                *
         *                                                    *
         *****************************************************************************************************************/
        EdgeToEdge.enable(this);                                                                    //

        // Cargamos el layout principal                                                                                  //
        setContentView(R.layout.activity_main);                                                                          //

        // Ajuste de padding dinámico para respetar system bars (status + nav) — evita overlaps.                         //
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {     //
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);                          //
            return insets;                                                                                               //
        });
        /*****************************************************************************************************************/


        // Inicializamos variables por defecto (vacías)
        nombreUsuario = "";
        contraseña = "";

        // Referencias a vistas por id (de activity_main.xml)
        input_usuario = findViewById(R.id.main__input_usuario);
        input_contrasena = findViewById(R.id.main__input_contrasena);
        btn_ingresar = findViewById(R.id.main__button_ingresar);

        // Instanciamos DBHelper (esto también asegura copiar DB desde assets si corresponde)
        dbHelper = new DBHelper(MainActivity.this);

        /**
         * Atención: aquí estás creando e insertando un usuario "admin" en cada onCreate.
         * - Útil para development/testing.
         * - Riesgo: si la tabla no evita duplicados, se crearán múltiples filas "admin".
         * - Recomendación: envolver en un getUserByUsername(...) y solo insertar si no existe.
         *
         *   +---------+
         *   | admin   |  <- usuario de prueba
         *   +---------+
         */


        populateTestUsers(10);

        // Listener del botón de ingresar
        btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Leemos los valores ingresados en los EditText
                nombreUsuario = input_usuario.getText().toString();
                contraseña = input_contrasena.getText().toString();

                // Consultamos localmente si existe un usuario con ese nombre + contraseña
                User usuarioIngresado = dbHelper.comprobarUsuarioLocal(nombreUsuario, contraseña);

                /*
                 * Lógica de navegación:
                 * - Si usuarioIngresado tiene id != -1 => existe en la DB => abrimos Principal.
                 * - Si no existe mostramos un Toast indicando que no existe.
                 *
                 * Nota de seguridad: comprobarUsuarioLocal compara contraseña en texto plano.
                 * Mejor usar hashes y validar con funciones seguras.
                 */
                if (usuarioIngresado.getId() != -1) {
                    // Usuario válido -> vamos a la pantalla principal
                    Intent intent = new Intent(MainActivity.this, Principal.class);
                    startActivity(intent);
                } else {
                    // Usuario inválido -> feedback al usuario
                    Toast.makeText(MainActivity.this, "Usuario no existe", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    /**
     * --- MÉTODO DE AYUDA PARA POBLAR LA BASE DE DATOS ---
     *
     * `populateTestUsers` es una función de utilidad diseñada para facilitar las pruebas de la aplicación.
     * Su objetivo es crear y añadir un número específico de usuarios de prueba a la base de datos
     * de forma automática.
     *
     * Una característica clave es que **evita crear duplicados**: antes de insertar un nuevo usuario,
     * comprueba si ya existe uno con el mismo nombre.
     *
     * Al final, muestra un resumen de cuántos usuarios se insertaron y cuántos se omitieron
     * (porque ya existían), tanto en el Logcat como en un mensaje Toast en pantalla.
     *
     * @param count La cantidad de usuarios de prueba que se intentarán crear.
     */
    private void populateTestUsers(int count) {
        // --- Validación Inicial ---
        // Si el número de usuarios a crear es cero o negativo, no hacemos nada y salimos del método.
        if (count <= 0) return;

        // --- Contadores ---
        // `inserted`: Lleva la cuenta de los nuevos usuarios que se han añadido con éxito.
        // `skipped`: Lleva la cuenta de los usuarios que se omitieron porque ya existían en la BD.
        int inserted = 0;
        int skipped = 0;

        // --- Bucle de Creación de Usuarios ---
        // Iteramos desde 1 hasta el número (`count`) de usuarios que queremos crear.
        for (int i = 1; i <= count; i++) {
            // Creamos un nombre de usuario único para cada iteración, ej: "usuario_test1", "usuario_test2", etc.
            String username = "usuario_test" + i;

            // --- Verificación de Duplicados ---
            // Antes de insertar, usamos el `DBHelper` para buscar si ya existe un usuario con este `username`.
            User existing = dbHelper.getUserByUsername(username);

            // Si `existing` no es nulo y su ID no es -1, significa que el usuario ya existe.
            if (existing != null && existing.getId() != -1) {
                skipped++; // Incrementamos el contador de omitidos.
                continue;  // `continue` salta al siguiente ciclo del bucle, ignorando el resto del código.
            }

            // --- Inserción del Nuevo Usuario ---
            // Si el usuario no existía, creamos un nuevo objeto `User`.
            // El ID se pone en 0 porque la base de datos lo generará automáticamente (autoincremental).
            User usuarioNuevo = new User(0, username, "pass" + i);

            // Llamamos a `addUser` para insertar el nuevo usuario en la base de datos.
            // El método devuelve el ID del nuevo usuario si fue exitoso, o -1 si hubo un error.
            long id = dbHelper.addUser(usuarioNuevo);

            // Verificamos el resultado de la inserción.
            if (id != -1) {
                inserted++; // Si el ID es válido, incrementamos el contador de insertados.
            } else {
                // Si hubo un error, lo registramos en el Logcat para depuración.
                Log.w("Prueba", "Falló la inserción del usuario: " + username);
            }
        }

        // --- Feedback Final ---
        // Creamos un mensaje que resume la operación.
        String message = "Usuarios de prueba: insertados=" + inserted + " omitidos=" + skipped;

        // Mostramos el mensaje en el Logcat (visible para desarrolladores en Android Studio).
        Log.i("Prueba", message);
        // Mostramos un mensaje Toast en la pantalla (visible para el usuario final).
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}