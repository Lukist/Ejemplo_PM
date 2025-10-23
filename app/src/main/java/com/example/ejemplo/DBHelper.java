package com.example.ejemplo;

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

public class DBHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "ejemplo.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    private static final String TABLE_USUARIO = "tabla_usuario";
    private static final String COL_ID = "id_usuario";
    private static final String COL_NOMBRE = "nombre_usuario";
    private static final String COL_PASSWORD = "password";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // Ensure DB exists or copy from assets if needed
        SQLiteDatabase db = getWritableDatabase();
        if (!checkDatabaseExistence(db)) {
            try {
                copyDatabase(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------- Database copy logic ----------------------

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

    // ---------------------- CRUD and Auth Methods ----------------------

    /**
     * Authenticate a user by username and password.
     * Returns a User object if found, otherwise a User with id = -1.
     */
    public User comprobarUsuarioLocal(String nombreUsuario, String password) {
        User user = new User();
        user.setId(-1); // default "not found" indicator

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE " + COL_NOMBRE + " = ? AND " + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreUsuario, password});

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
     * Add a new user to the database.
     * Returns the new row ID, or -1 if failed.
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
     * Get a user by username.
     * Returns a User object if found, otherwise User with id = -1.
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
     * Update the password of an existing user.
     * Returns true if at least one row was updated.
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
     * Delete a user by username.
     * Returns the number of rows deleted.
     */
    public int deleteUser(String nombreUsuario) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USUARIO, COL_NOMBRE + " = ?", new String[]{nombreUsuario});
        db.close();
        return rowsDeleted;
    }
}
