package com.example.dam.listacontactos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

public class ListaContactos extends AppCompatActivity {


    List<Contacto> contactos;
    Adaptador adaptador;
    ImageView mas;
    Contacto cont;
    TextView tvTexto;
    CheckBox cbSincro;
    SharedPreferences sincro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__contactos);
        try {
            iniciar();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mas = (ImageView)findViewById(R.id.ivMasOpciones);
        tvTexto = (TextView)findViewById(R.id.tvTextoCopia);
        cbSincro = (CheckBox)findViewById(R.id.cbSincro);
        sincro = getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista__contactos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nuevoContacto:

                AlertDialog.Builder alert= new AlertDialog.Builder(this);
                alert.setTitle(R.string.nuevo);
                LayoutInflater inflater= LayoutInflater.from(this);
                final View vista = inflater.inflate(R.layout.dialogo_nuevo, null);
                alert.setView(vista);
                alert.setPositiveButton(R.string.nuevo,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                long id = contactos.size() - 1;
                                EditText etN, etTel;
                                etN = (EditText) vista.findViewById(R.id.etNuevoNombre);
                                etTel = (EditText) vista.findViewById(R.id.etNuevoTelef);

                                List<String> telf = new ArrayList<>();
                                Contacto c = new Contacto(id, etN.getText().toString(), telf);
                                c.addTelefono(etTel.getText().toString());
                                contactos.add(c);
                                Collections.sort(contactos);
                                adaptador.notifyDataSetChanged();
                            }
                        });
                alert.setNegativeButton(R.string.cancelar, null);
                alert.show();
                return true;
            case R.id.asc:
                Collections.sort(contactos);
                adaptador.notifyDataSetChanged();
                return true;
            case R.id.desc:
                Collections.sort(contactos, Collections.reverseOrder());
                adaptador.notifyDataSetChanged();
                return true;
            case R.id.copia:
                cbSincro = (CheckBox)findViewById(R.id.cbSincro);
                AlertDialog.Builder a= new AlertDialog.Builder(this);
                a.setTitle("Copia de seguridad");
                LayoutInflater in= LayoutInflater.from(this);
                final View v = in.inflate(R.layout.dialogo_copia, null);
                a.setView(v);
                boolean i = cbSincro.isChecked();
                if (!i) {
                    SharedPreferences.Editor editor = sincro.edit();
                    editor.putString("sincro", "desactivada");
                    editor.apply();
                }else {
                    SharedPreferences.Editor editor = sincro.edit();
                    editor.putString("sincro", "activada");
                    editor.apply();
                }

                a.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void iniciar() throws IOException {
        contactos = Contacto.getListaContactos(this);

        //Soluciona el problema del adaptador que sólo escribe los primeros teléfonos que aparecen en pantalla
        for (Contacto element : contactos) {
            element.setTelefonos(Contacto.getListaTelefonos(getApplicationContext(), element.getId()));
        }
        //----------------^

        adaptador = new Adaptador(this, contactos);
        final ListView lv = (ListView)findViewById(R.id.lvContactos);
        lv.setAdapter(adaptador);

        // CheckBox para la sincronizacion
        sincro = getPreferences(Context.MODE_PRIVATE);
        String activar = sincro.getString("sincro", "activida");
        switch (activar){
            case "activada":
                copiar(null);
                cbSincro.setChecked(true);
                break;
            case "desactivada":
                cbSincro.setChecked(false);
                break;
        }

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ListaContactos.this);
                dialogo1.setTitle(R.string.confirmar_eliminar);
                dialogo1.setMessage(contactos.get(position).getNombre());
                dialogo1.setCancelable(false);

                dialogo1.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        String dato = contactos.get(position).getNombre();
                        Toast.makeText(ListaContactos.this, dato, Toast.LENGTH_SHORT).show();

                        contactos.remove(position);
                        adaptador.notifyDataSetChanged();
                    }
                });
                dialogo1.setNegativeButton(R.string.cancelar, null);
                dialogo1.show();
                return true;
            }
        });
    }

    public void dialogo(View v){
        final ListView lv = (ListView)findViewById(R.id.lvContactos);
        final int position =  lv.getPositionForView(v);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.detalles);
        LayoutInflater inflater = LayoutInflater.from(this);
        int res = R.layout.dialogo_principal;
        final View vista = inflater.inflate(res, null);
        alert.setView(vista);

        TextView tv1 = (TextView) vista.findViewById(R.id.tvDetallesNombre);
        cont = contactos.get(position);
        tv1.setText(cont.getNombre());

        TextView tv2 = (TextView) vista.findViewById(R.id.tvDialogo);
        String str = cont.getTelefonos().toString();
        tv2.setText(str);

        alert.show();
    }

    public void editar(View v) {
        final ListView lv = (ListView)findViewById(R.id.lvContactos);
        final int position =  lv.getPositionForView(v);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.editar);
        LayoutInflater inflater = LayoutInflater.from(this);
        int res = R.layout.dialogo_editar;
        final View vista = inflater.inflate(res, null);
        alert.setView(vista);

        EditText et1 = (EditText) vista.findViewById(R.id.etNombre);

        cont = contactos.get(position);
        et1.setText(cont.getNombre());

        EditText ed2 = (EditText) vista.findViewById(R.id.etTelf);
        ed2.setText(Contacto.formatear(cont.getTelefonos().toString(), "[]"));

        alert.setCancelable(false);
        alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                EditText et1 = (EditText) vista.findViewById(R.id.etNombre);
                String nombre = et1.getText().toString();

                EditText ed2 = (EditText) vista.findViewById(R.id.etTelf);
                String telf = ed2.getText().toString();
                cont.setNombre(nombre);

                List<String> listaTelf = new ArrayList<>();
                listaTelf.add(telf);
                cont.setTelefonos(listaTelf);

                adaptador.notifyDataSetChanged();
            }
        });
        alert.setNegativeButton(R.string.cancelar, null);
        alert.show();
    }

    public void copiar(View view) throws IOException {
        FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null), "backup.xml"));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, true);
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        List<Contacto> l = contactos;
        docxml.startTag(null, "contactos");
        int position = 0;
        for (Contacto s:l){
            s = contactos.get(position);
            s.setTelefonos(s.getTelefonos());
            docxml.startTag(null, "contacto");
            docxml.startTag(null, "nombre");
            docxml.attribute(null, "id", String.valueOf(s.getId()));
            Log.v("Id: ", String.valueOf(s.getId()));
            docxml.text(s.getNombre());
            Log.v("Nombre: ", s.getNombre());
            docxml.endTag(null, "nombre");

            //Separa cada número mediante las comas
            String telefonos = Contacto.formatear(s.getTelefonos().toString(), "[]");
            String[] telefArray = telefonos.split(",");

            for (int i = 0; i < telefArray.length; i++) {
                docxml.startTag(null, "telefono");

                docxml.text(telefArray[i]);

                //Escribe todos los numeros en una etiqueta// docxml.text(s.getTelefonos().toString());
                docxml.endTag(null, "telefono");
            }
            docxml.endTag(null, "contacto");
            position+=1;
        }
        docxml.endDocument();
        docxml.flush();
        fosxml.close();
        setTvTexto("Copia realizada exitosamente.");
    }

    public void mostrar(View view) throws IOException, XmlPullParserException {
        XmlPullParser lectorxml = Xml.newPullParser();
        lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null), "backup.xml")), "utf-8");
        int evento = lectorxml.getEventType();
        while (evento != XmlPullParser.END_DOCUMENT){
            if (evento == XmlPullParser.START_TAG){
                String etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("nombre")==0){
                    String atrib = lectorxml.getAttributeValue(null, "id");
                    String texto = lectorxml.nextText();
                    tvTexto.append("Id: " + atrib + " Nombre: " + texto + "\n");
                }else if (etiqueta.compareTo("telefono")==0){
                    String texto = lectorxml.nextText();
                    tvTexto.append("    Telefono: " + texto + "\n");
                }
            }
            evento = lectorxml.next();
        }
    }
    private void setTvTexto(String texto){
        tvTexto.setText(texto);
    }
}
