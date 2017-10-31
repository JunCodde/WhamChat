package com.apps.juncode.pruebawham.Model;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public class Notif {

    private String titulo, descripcion,id;

    public Notif() {
    }

    public Notif(String titulo, String descripcion, String id) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
