package com.example.chat;

public class Mensaje {
    private String Mensaje , Nombre,FotoPerfil,TypeMensaje,Hora,UrlFoto,UrlAudio;

    public Mensaje() {

    }

    public Mensaje(String mensaje, String nombre, String fotoPerfil, String typeMensaje, String hora, String urlFoto, String urlAudio) {
        Mensaje = mensaje;
        Nombre = nombre;
        FotoPerfil = fotoPerfil;
        TypeMensaje = typeMensaje;
        Hora = hora;
        UrlFoto = urlFoto;
        UrlAudio = urlAudio;
    }

    public Mensaje(String mensaje, String nombre, String fotoPerfil, String typeMensaje, String hora, String urlFoto) {
        Mensaje = mensaje;
        Nombre = nombre;
        FotoPerfil = fotoPerfil;
        TypeMensaje = typeMensaje;
        Hora = hora;
        UrlFoto = urlFoto;
    }


    public Mensaje(String mensaje, String nombre, String fotoPerfil, String typeMensaje, String hora) {
        Mensaje = mensaje;
        Nombre = nombre;
        FotoPerfil = fotoPerfil;
        TypeMensaje = typeMensaje;
        Hora = hora;
    }

    public String getUrlAudio() {
        return UrlAudio;
    }

    public void setUrlAudio(String urlAudio) {
        UrlAudio = urlAudio;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getFotoPerfil() {
        return FotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        FotoPerfil = fotoPerfil;
    }

    public String getTypeMensaje() {
        return TypeMensaje;
    }

    public void setTypeMensaje(String typeMensaje) {
        TypeMensaje = typeMensaje;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public String getUrlFoto() {
        return UrlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        UrlFoto = urlFoto;
    }
}
