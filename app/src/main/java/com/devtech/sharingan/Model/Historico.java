package com.devtech.sharingan.Model;

public class Historico {

    private int id;
    private String texto;
    private String data;

    public Historico() {

    }

    public Historico(int id, String texto, String data) {
        this.id = id;
        this.texto = texto;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
