package com.example.chat.Objetos;

public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private String foto;
    private String estado;
    private String fecha;
    private String hora;
    private int solicitudes;
    private int NuevoMensaje;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String correo, String foto, String estado, String fecha, String hora, int solicitudes, int nuevoMensaje) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        this.estado = estado;
        this.fecha = fecha;
        this.hora = hora;
        this.solicitudes = solicitudes;
        NuevoMensaje = nuevoMensaje;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(int solicitudes) {
        this.solicitudes = solicitudes;
    }

    public int getNuevoMensaje() {
        return NuevoMensaje;
    }

    public void setNuevoMensaje(int nuevoMensaje) {
        NuevoMensaje = nuevoMensaje;
    }
}
