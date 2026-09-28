package it.unimib.sd2025.model;

public class User {
    private String nome;
    private String cognome;
    private float saldo;
    private String email;
    private String codiceFiscale;
    private Buono[] buoni;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public Buono[] getBuoni() {
        return buoni;
    }

    public void setBuoni(Buono[] buoni) {
        this.buoni = buoni;
    }
}
