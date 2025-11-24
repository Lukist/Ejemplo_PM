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

public class UsersAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private Context mContext;
    private int resourceLayout;

    public UsersAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        users = objects;
        mContext = context;
        resourceLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(resourceLayout, parent, false);
        }

        User user = users.get(position);

        TextView usuarioNombre = view.findViewById(R.id.usuario_fila__nombre);
        TextView usuarioId = view.findViewById(R.id.usuario_fila__ID);
        TextView usuarioExtra = view.findViewById(R.id.usuario_fila__extra);


        usuarioNombre.setText(user.getNombreUsuario() != null ? user.getNombreUsuario() : "—");
        usuarioId.setText(String.valueOf(user.getId()));
        usuarioExtra.setText("");

        return view;
    }
}