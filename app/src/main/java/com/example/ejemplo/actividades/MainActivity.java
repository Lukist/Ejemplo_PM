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

    private void populateTestUsers(int count) {
        if (count <= 0) return;

        int inserted = 0;
        int skipped = 0;

        for (int i = 1; i <= count; i++) {
            String username = "usuario_test" + i;
            User existing = dbHelper.getUserByUsername(username);
            if (existing != null && existing.getId() != -1) {
                skipped++;
                continue;
            }

            User usuarioNuevo = new User(0, username, "pass" + i);
            long id = dbHelper.addUser(usuarioNuevo);
            if (id != -1) {
                inserted++;
            } else {
                Log.w("Prueba", "Failed to insert user: " + username);
            }
        }


        String message = "Test users: inserted=" + inserted + " skipped=" + skipped;
        Log.i("Prueba", message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}