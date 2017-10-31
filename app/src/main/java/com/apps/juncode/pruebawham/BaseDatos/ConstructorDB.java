package com.apps.juncode.pruebawham.BaseDatos;

import android.content.ContentValues;
import android.content.Context;

import com.apps.juncode.pruebawham.Model.User;

import java.util.ArrayList;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public class ConstructorDB {

    private Context context;

    public ConstructorDB(Context context){
        this.context = context;
    }

    public boolean checkIfUserExist(){
        BaseDatos db = new BaseDatos(context);

        return db.chekIfUserExist();

    }

    public void insertarUsuario(User u){
        BaseDatos db = new BaseDatos(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantesDB.TABLA_USUARIO_NOMBRE, u.getNombre());
        contentValues.put(ConstantesDB.TABLA_USUARIO_CORREO, u.getCorreo());
        contentValues.put(ConstantesDB.TABLA_USUARIO_TOKEN, u.getToken());
        contentValues.put(ConstantesDB.TABLA_USUARIO_UID, u.getUID());
        contentValues.put(ConstantesDB.TABLA_USUARIO_FOTO, u.getFoto());
        contentValues.put(ConstantesDB.TABLA_USUARIO_ACTIVO, u.getActivo());
        db.insertarUsuario(contentValues);
        db.close();
    }

    public void insertarContacto(User c){

        BaseDatos db = new BaseDatos(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantesDB.TABLA_CONTACTOS_NOMBRE, c.getNombre());
        contentValues.put(ConstantesDB.TABLA_CONTACTOS_CORREO, c.getCorreo());
        contentValues.put(ConstantesDB.TABLA_CONTACTOS_UID, c.getUID());
        contentValues.put(ConstantesDB.TABLA_CONTACTOS_TOKEN, c.getToken());
        contentValues.put(ConstantesDB.TABLA_CONTACTOS_FOTO, c.getFoto());
        contentValues.put(ConstantesDB.TABLA_CONTACTOS_ACTIVO, c.getActivo());
        db.insertarContacto(contentValues);
        db.close();

    }

    public ArrayList<User> obtenerTodosLosContactos(){
        BaseDatos db = new BaseDatos(context);

        return db.obtenerTodosLosContactos();
    }

    public void BorrarContactos(){
        BaseDatos db = new BaseDatos(context);
        db.borrarContactos();
    }


}
