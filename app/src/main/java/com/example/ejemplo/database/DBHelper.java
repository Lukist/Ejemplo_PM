package com.example.ejemplo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ejemplo.modelos.User;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
     ██████╗ ██████╗     ██╗  ██╗███████╗██╗     ██████╗ ███████╗██████╗
     ██╔══██╗██╔══██╗    ██║  ██║██╔════╝██║     ██╔══██╗██╔════╝██╔══██╗
     ██║  ██║██████╔╝    ███████║█████╗  ██║     ██████╔╝█████╗  ██████╔╝
     ██║  ██║██╔══██╗    ██╔══██║██╔══╝  ██║     ██╔═══╝ ██╔══╝  ██╔══██╗
     ██████╔╝██████╔╝    ██║  ██║███████╗███████╗██║     ███████╗██║  ██║
     ╚═════╝ ╚═════╝     ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚══════╝╚═╝  ╚═╝
 *
 Pequeña descripcion:

 Esta es una clase diseñada para manejar la base de datos, su intencion es la de clonar una base de datos colocada dentro de
 la carpeta: app/src/main/assets/databases.
 Todas las funciones correspondientes a su escritura y lectura, asi como la edicion y la eliminacion de atributos se manejan desde aca.

 La intencion de este archivo es el de proporcionarles una forma de crear y manejar sus bases de datos simplemente copiando y pegando la primera parte de este archivo
 */

/**
 * Como pueden ver esta clase extendiende de algo llamado SQLiteAssetHelper. Para poder acceder a esta libreria deben de importarla desde el build.gradle en
 * la seccion de dependencies.
 *
 * implementation 'com.readystatesoftware.sqliteasset:sqliteassethelper:+'
 */

public class DBHelper extends SQLiteAssetHelper {


    /**
     * Si quieren reutilizar esta base de datos copien desde aqui
     *
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * ---------------------------------------------------------------------------------------------
     */
    private static final String DATABASE_NAME = "ejemplo.db"; // Este nombre depende de la database guardada en app/src/main/assets/databases.
    private static final int DATABASE_VERSION = 1; // esto casi nunca se cambia

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        SQLiteDatabase db = getWritableDatabase();
        if (!checkDatabaseExistence(db)) {
            try {
                copyDatabase(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean checkDatabaseExistence(SQLiteDatabase db) {
        String path = db.getPath();
        File file = new File(path);
        return file.exists();
    }
    private void copyDatabase(Context context) throws IOException {
        close();
        OutputStream output = new FileOutputStream(getDatabasePath(context));
        InputStream input = context.getAssets().open("databases/" + DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        input.close();
        output.flush();
        output.close();
    }
    private String getDatabasePath(Context context) {
        return context.getDatabasePath(DATABASE_NAME).getPath();
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * Hasta aqui
     */


    /**
     * Aca solo se declaran nombres de tablas para ahorrar codigo-----------------------------------//
     */                                                                                             //
    private static final String TABLE_USUARIO = "tabla_usuario";                                    //
    private static final String COL_ID = "id_usuario";                                              //
    private static final String COL_NOMBRE = "nombre_usuario";                                      //
    private static final String COL_PASSWORD = "password";                                          //

    // ---------------------------------------------------------------------------------------------//


    /**
     * Desde aca en adelante son los metodos encargados de manejar la logica de escritura y lectura. Esto siempre se adapta
     * a las tablas personalizadas que hayan creado ustedes
     */

    /**
     * comprobarUsuarioLocal:
     * - Busca en la tabla de usuarios un registro que coincida con el nombre y password dados.
     * - Si lo encuentra rellena y devuelve un objeto User con los datos.
     * - Si no lo encuentra devuelve un User con id = -1 (indicador "no encontrado").
     *
     * NOTAS DE SEGURIDAD:
     * - Este ejemplo compara contraseñas en texto plano (como viene de la DB). En la realidad es recomendable
     *   almacenar y comparar hashes en lugar de passwords en claro. ESTO NO IMPORTA PARA EL TRANSCURSO DE ESTA CLASE.
     */
    public User comprobarUsuarioLocal(String nombreUsuario, String password) {
        User user = new User();
        user.setId(-1); // default "not found" indicator

        // Obtenemos DB en modo lectura
        SQLiteDatabase db = getReadableDatabase();

        // Query parametrizada para evitar inyección SQL
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE " + COL_NOMBRE + " = ? AND " + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreUsuario, password});

        // Si el cursor tiene resultados, movemos al primero y leemos columnas
        if (cursor != null && cursor.moveToFirst()) {
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
            user.setNombreUsuario(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)));
        }

        // Cerramos cursor y DB para liberar recursos
        if (cursor != null) cursor.close();
        db.close();

        return user;
    }

    /**
     * addUser:
     * - Inserta un nuevo usuario en la tabla.
     * - Devuelve el id generado por sqlite (long) o -1 si hubo error/entrada nula.
     *
     * Uso:
     * User u = new User(); u.setNombreUsuario(...); u.setPassword(...);
     * long id = dbHelper.addUser(u);
     *
     *   +------------+
     *   | Nuevo user |
     *   +------------+
     *       \/
     */
    public long addUser(User user) {
        if (user == null) return -1;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, user.getNombreUsuario());
        values.put(COL_PASSWORD, user.getPassword());

        long id = db.insert(TABLE_USUARIO, null, values);
        db.close();
        return id;
    }

    /**
     * getUserByUsername:
     * - Busca un usuario por su nombre y devuelve un objeto User con los datos.
     * - Si no existe, devuelve un User con id = -1 (indicador "no encontrado").
     */
    public User getUserByUsername(String nombreUsuario) {
        User user = new User();
        user.setId(-1);

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE " + COL_NOMBRE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreUsuario});

        if (cursor != null && cursor.moveToFirst()) {
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
            user.setNombreUsuario(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)));
        }

        if (cursor != null) cursor.close();
        db.close();

        return user;
    }

    /**
     * updatePassword:
     * - Actualiza la contraseña del usuario que coincida con nombreUsuario.
     * - Devuelve true si se actualizó al menos una fila, false si no.
     *
     *   (cambiar pass)
     *    [nombre] --> [nueva pass]
     */
    public boolean updatePassword(String nombreUsuario, String nuevaPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, nuevaPassword);

        int rowsUpdated = db.update(TABLE_USUARIO, values, COL_NOMBRE + " = ?", new String[]{nombreUsuario});
        db.close();
        return rowsUpdated > 0;
    }

    /**
     * deleteUser:
     * - Elimina usuarios cuyo nombre coincida con el pasado por parametro.
     * - Devuelve el numero de filas eliminadas (0 si no hubo coincidencias).
     *
     * Uso: int borrados = dbHelper.deleteUser("pepe");
     *
     *   ( ¯\_(ツ)_/¯ )  -> Se borra para siempre
     */
    public int deleteUser(String nombreUsuario) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USUARIO, COL_NOMBRE + " = ?", new String[]{nombreUsuario});
        db.close();
        return rowsDeleted;
    }
}
