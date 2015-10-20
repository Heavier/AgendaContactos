package com.example.dam.listacontactos;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

public class Contacto implements Serializable, Comparable<Contacto> {
    private long id;
    private String nombre;
    private List<String> telefonos;

    public Contacto(long id, String nombre, List<String> telefonos) {
        this.id = id;
        this.nombre = nombre;
        this.telefonos = telefonos;
    }

    public Contacto() {
        this(0, "", new ArrayList<String>());
    }

    public String getTelefono(int location) {
        return telefonos.get(location);
    }

    public void setTelefono(int location, String telefono) {
        this.telefonos.set(location, telefono);
    }

    public int size() {
        return telefonos.size();
    }

    public boolean isEmpty() {
        return telefonos.isEmpty();
    }

    public boolean addTelefono(String object) {
        return telefonos.add(object);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<String> telefonos) {
        this.telefonos = telefonos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;

        return id == contacto.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }


    @Override
    public int compareTo(Contacto contacto) {
        int r = this.nombre.compareTo(contacto.nombre);
        if (r == 0) {
            r = (int) (this.id - contacto.id);
        }
        return r;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefonos=" + telefonos +
                '}';
    }

    //---------------------------------------------------------------------------------------------
    public static List<Contacto> getListaContactos(Context contexto) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1", "1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        while (cursor.moveToNext()) {
            contacto = new Contacto();
            contacto.setId(cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));
            lista.add(contacto);
        }
        /*Nueva línea para evitar los errores durante la ejecucion*/
        cursor.close();

        return lista;
    }

    public static List<String> getListaTelefonos(Context contexto, long id) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id + ""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while (cursor.moveToNext()) {
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
        }
        /*Nueva línea para evitar los errores durante la ejecucion*/
        cursor.close();
        return lista;
    }
//----------------------------Este método se creó para obetener un único número del contacto pero no es eficiente-------------------------

    public static List<String> getUnTelefono(Context contexto, long id) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id + ""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        int i = 1;
        while (cursor.moveToNext() && i == 1) {
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
            i = 2;
        }
        /*Nueva línea para evitar los errores durante la ejecucion*/
        cursor.close();
        return lista;
    }

    //-------------------------Eliminar los corchetes----------------
    public static String formatear(String s_cadena, String s_caracteres) {
        String nueva_cadena = "";
        Character caracter = null;
        boolean valido = true;

        for (int i = 0; i < s_cadena.length(); i++) {
            valido = true;
            for (int j = 0; j < s_caracteres.length(); j++) {
                caracter = s_caracteres.charAt(j);

                if (s_cadena.charAt(i) == caracter) {
                    valido = false;
                    break;
                }
            }
            if (valido)
                nueva_cadena += s_cadena.charAt(i);
        }

        return nueva_cadena;
    }


    public static String getFirstWord(String text) {
        if (text.indexOf(',') > -1) { // Check if there is more than one word.
            return text.substring(0, text.indexOf(',')); // Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
    }
}