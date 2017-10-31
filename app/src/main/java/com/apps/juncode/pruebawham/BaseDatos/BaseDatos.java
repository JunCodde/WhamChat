package com.apps.juncode.pruebawham.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.apps.juncode.pruebawham.Model.User;

import java.util.ArrayList;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public class BaseDatos extends SQLiteOpenHelper {

    private static final String TAG = "Base datos";
    private Context context;

    public BaseDatos(Context context) {
        super(context, ConstantesDB.DATABASE_NAME, null, ConstantesDB.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryCrearTablaUsuario = "CREATE TABLE "         + ConstantesDB.TABLA_USUARIO + " (" +
                ConstantesDB.TABLA_USUARIO_NOMBRE               + " TEXT, " +
                ConstantesDB.TABLA_USUARIO_CORREO               + " TEXT, " +
                ConstantesDB.TABLA_USUARIO_TOKEN                + " TEXT, " +
                ConstantesDB.TABLA_USUARIO_UID                  + " TEXT, " +
                ConstantesDB.TABLA_USUARIO_FOTO                 + " TEXT, " +
                ConstantesDB.TABLA_USUARIO_ACTIVO               + " TEXT"   +
                ") ";

        db.execSQL(queryCrearTablaUsuario);

        String queryCrearTablaContactos = "CREATE TABLE "       + ConstantesDB.TABLA_CONTACTOS + " (" +
                ConstantesDB.TABLA_CONTACTOS_NOMBRE             + " TEXT, " +
                ConstantesDB.TABLA_CONTACTOS_CORREO             + " TEXT, " +
                ConstantesDB.TABLA_CONTACTOS_UID                + " TEXT, " +
                ConstantesDB.TABLA_CONTACTOS_TOKEN              + " TEXT, " +
                ConstantesDB.TABLA_CONTACTOS_FOTO               + " TEXT, " +
                ConstantesDB.TABLA_CONTACTOS_ACTIVO             + " TEXT" +
                ") ";

        db.execSQL(queryCrearTablaContactos);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXIST " + ConstantesDB.TABLA_USUARIO);
        db.execSQL("DROP TABLE IF EXIST " + ConstantesDB.TABLA_CONTACTOS);

    }

    public void borrarContactos(){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ConstantesDB.TABLA_CONTACTOS, null, null);

    }

    public User obtenerUsuario(){

        User user = new User();

        String columnas[] = {ConstantesDB.TABLA_USUARIO_NOMBRE, ConstantesDB.TABLA_USUARIO_CORREO, ConstantesDB.TABLA_USUARIO_TOKEN, ConstantesDB.TABLA_USUARIO_UID
                , ConstantesDB.TABLA_USUARIO_FOTO, ConstantesDB.TABLA_USUARIO_ACTIVO};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(ConstantesDB.TABLA_USUARIO, columnas, null, null, null, null, null);

        if(c.moveToFirst()){

            c.moveToLast();

            user.setNombre(c.getString(0));
            user.setCorreo(c.getString(1));
            user.setToken(c.getString(2));
            user.setUID(c.getString(3));
            user.setFoto(c.getString(4));
            user.setActivo(c.getString(5));

        }else{
            Log.d(TAG, "No existen registros");
        }

        db.close();
        return user;
    }

    public ArrayList<User> obtenerTodosLosContactos(){
        ArrayList<User> contactos = new ArrayList<>();

        String query = "SELECT * FROM " + ConstantesDB.TABLA_CONTACTOS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor registros = db.rawQuery(query, null);

        while(registros.moveToNext()){
            User currentContact = new User();
            currentContact.setNombre(registros.getString(0));
            currentContact.setCorreo(registros.getString(1));
            currentContact.setUID(registros.getString(2));
            currentContact.setToken(registros.getString(3));
            currentContact.setFoto(registros.getString(4));
            currentContact.setActivo(registros.getString(5));

            contactos.add(currentContact);

        }
        db.close();

        return contactos;
    }

    public boolean chekIfUserExist(){

        String columnas[] = {ConstantesDB.TABLA_USUARIO_NOMBRE, ConstantesDB.TABLA_USUARIO_CORREO, ConstantesDB.TABLA_USUARIO_TOKEN, ConstantesDB.TABLA_USUARIO_UID
                , ConstantesDB.TABLA_USUARIO_FOTO, ConstantesDB.TABLA_USUARIO_ACTIVO};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(ConstantesDB.TABLA_USUARIO, columnas, null, null, null, null, null);

        if(c.moveToLast()){

            return true;

        }else{

            return false;

        }

    }

    public void insertarUsuario(ContentValues contentValues){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(ConstantesDB.TABLA_USUARIO, null, contentValues);
        db.close();
    }

    public  void insertarContacto(ContentValues contentValues){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(ConstantesDB.TABLA_CONTACTOS, null, contentValues);
        db.close();

    }


}
