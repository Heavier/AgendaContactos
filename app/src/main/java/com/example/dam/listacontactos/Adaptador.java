package com.example.dam.listacontactos;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Adaptador extends ArrayAdapter<Contacto> {

    private int position;

    public Adaptador(Context context, List<Contacto> objects) {
        super(context, R.layout.elemento, objects);
    }

    static class ViewHolder {
        public TextView tvNombre, tvNumero, tvDialogo;
        public ImageView imv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contacto contacto = getItem(position);
        this.position = position;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.elemento, parent, false);
            viewHolder.tvNombre = (TextView) convertView.findViewById(R.id.tvNombre);
            viewHolder.tvNumero = (TextView) convertView.findViewById(R.id.tvNumero);
            viewHolder.tvDialogo = (TextView) convertView.findViewById(R.id.tvDialogo);

            viewHolder.imv = (ImageView) convertView.findViewById(R.id.ivMasOpciones);
            viewHolder.imv.setTag(position);

            contacto.setTelefonos(Contacto.getListaTelefonos(getContext(), contacto.getId()));
            Log.v("1: ", contacto.toString());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            Log.v("2: ", contacto.toString());
        }

        viewHolder.tvNombre.setText(contacto.getNombre());

        String telefonos = Contacto.formatear(contacto.getTelefonos().toString(), "[]");
        String[] telefArray = telefonos.split(",");

        //for (int i = 0; i < 1; i++) {
            viewHolder.tvNumero.setText(telefArray[0]);
        //}

        Log.v("3: ", contacto.toString());

        return convertView;
    }
}