package com.example.ejemplo.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ejemplo.R;
import com.example.ejemplo.modelos.User;

import java.util.List;

/**
 * Un Adaptador es un componente crucial en Android que sirve como puente entre una fuente de datos
 * (como una lista de usuarios) y una vista de colección (como un ListView o un RecyclerView).
 * Su principal responsabilidad es "adaptar" cada objeto de la fuente de datos a una vista individual (una fila).
 *
 * Esta clase, `UsersAdapter`, extiende `ArrayAdapter<User>`, especializándose en manejar una lista de objetos `User`.
 */
public class UsersAdapter extends ArrayAdapter<User> {

    // --- MIEMBROS DE LA CLASE ---

    /**
     * `mContext`:
     * Guarda la referencia al contexto de la actividad que está usando este adaptador.
     * El contexto es necesario para operaciones como "inflar" layouts.
     */
    private Context mContext;

    /**
     * `resourceLayout`:
     * Almacena el ID del archivo de layout XML que define la apariencia de CADA fila
     * en el ListView. Por ejemplo, `R.layout.row_usuario`.
     */
    private int resourceLayout;

    /**
     * `users`:
     * Una copia de la lista de objetos `User` que el adaptador mostrará.
     */
    private List<User> users;


    /**
     * --- CONSTRUCTOR ---
     * El constructor inicializa el adaptador con todo lo que necesita para funcionar.
     *
     * @param context El contexto de la aplicación/actividad (ej. `MainActivity.this`).
     * @param resource El ID del layout para cada fila (ej. `R.layout.row_usuario`).
     * @param objects La lista de datos (los usuarios) que se van a mostrar.
     */
    public UsersAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        // La llamada a `super` es fundamental. Pasa los parámetros básicos a la clase base `ArrayAdapter`.
        super(context, resource, objects);

        // Guardamos las referencias en nuestras variables miembro para usarlas más tarde, especialmente en `getView`.
        this.mContext = context;
        this.resourceLayout = resource;
        this.users = objects;
    }


    /**
     * --- MÉTODO PRINCIPAL: getView ---
     *
     * Este es el corazón del adaptador. Android llama a este método para cada elemento de la lista
     * que necesita ser mostrado en pantalla. Su trabajo es crear y configurar una vista (una fila)
     * para una posición específica.
     *
     * @param position La posición del elemento en la lista de datos (`users`) para el que se debe crear la vista. Empieza en 0.
     * @param convertView La vista "vieja" o reciclada. Si no es nula, podemos reutilizarla para mejorar el rendimiento.
     *                    Esto es clave para que listas largas funcionen de forma fluida.
     * @param parent El `ViewGroup` padre al que esta vista se adjuntará (normalmente, el propio `ListView`).
     *
     * @return La vista (ya configurada con los datos) que representa una única fila en el ListView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        // `convertView` es una vista que ya no está en pantalla (por ejemplo, porque el usuario hizo scroll).
        // En lugar de crear una nueva vista desde cero cada vez (lo cual es costoso),
        // podemos reutilizar `convertView` si existe.

        View view = convertView; // Empezamos asumiendo que podemos reciclar la vista.

        if (view == null) {
            // Si `view` (o `convertView`) es nulo, significa que no hay una vista para reciclar.
            // Esto suele pasar con las primeras filas que se muestran en pantalla.
            // Por lo tanto, debemos "inflar" (crear) una nueva vista desde nuestro archivo XML.

            // `LayoutInflater` es el servicio de Android que convierte un archivo XML de layout en un objeto View en memoria.
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(resourceLayout, parent, false);
        }

        // En este punto, `view` ya no es nulo. O era una vista reciclada o una recién creada.

        // --- VINCULACIÓN DE DATOS (DATA BINDING) ---

        // 1. Obtener el objeto de datos para esta posición.
        User user = users.get(position);

        // 2. Encontrar las vistas (Widgets) dentro del layout de la fila.
        // Usamos `view.findViewById` porque estamos buscando dentro del layout de la fila, no de toda la actividad.
        TextView usuarioNombre = view.findViewById(R.id.usuario_fila__nombre);
        TextView usuarioId = view.findViewById(R.id.usuario_fila__ID);
        TextView usuarioExtra = view.findViewById(R.id.usuario_fila__extra); // Aunque no se use, lo encontramos.

        // 3. Poblar las vistas con los datos del objeto `user`.
        // Es una buena práctica verificar si los datos son nulos para evitar errores.
        usuarioNombre.setText(user.getNombreUsuario() != null ? user.getNombreUsuario() : "—");
        usuarioId.setText(String.valueOf(user.getId())); // Los IDs suelen ser números, `setText` espera un String.
        usuarioExtra.setText(""); // Dejamos este campo vacío como en el código original.

        // 4. Devolver la vista final.
        // El `ListView` tomará esta vista y la mostrará en la pantalla en la posición correspondiente.
        return view;
    }
}