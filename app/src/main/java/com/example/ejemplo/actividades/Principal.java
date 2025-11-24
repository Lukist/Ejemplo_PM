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

public class Principal extends AppCompatActivity {

    private ListView listaUsuarios;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaUsuarios = findViewById(R.id.lista_usuarios);
        dbHelper = new DBHelper(this);

        // Synchronous load on main thread as requested:
        List<User> users = dbHelper.getAllUsersSafe(); // <-- correct method from DBHelper
        UsersAdapter adapter = new UsersAdapter(Principal.this, R.layout.row_usuario, users);
        listaUsuarios.setAdapter(adapter);


    }
}