package com.android.guicelebrini.qualrole.model;

public class City {
    private String id;
    private String nome;

    public City(String id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {

        //String toString = "Minha cidade é " + nome + " e seu ID é " + id;
        String toString = this.nome;

        return toString;
    }
}
