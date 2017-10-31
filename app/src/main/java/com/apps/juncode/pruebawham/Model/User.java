package com.apps.juncode.pruebawham.Model;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public class User {

    private String token, UID, nombre, correo, foto, activo;

    public User(String token, String UID, String nombre, String correo, String foto, String activo){
        this.token = token;
        this.UID = UID;
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        this.activo = activo;
    }

    public User() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }
}
