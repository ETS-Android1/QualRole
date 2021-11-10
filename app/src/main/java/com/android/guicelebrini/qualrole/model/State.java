package com.android.guicelebrini.qualrole.model;

public class State {

    private String id;
    private String sigla;
    private String nome;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {

        //String toString = "Meu estado é " + nome + "("+ sigla +") e seu id é " + id;
        String toString = this.sigla;

        return toString;
    }
}
