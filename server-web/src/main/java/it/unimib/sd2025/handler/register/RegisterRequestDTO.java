package it.unimib.sd2025.handler.register;

public class RegisterRequestDTO {
    private String CF;
    private String nome;
    private String cognome;
    private String email;

    /* Costruttore vuoto necessario per il binding */
    public RegisterRequestDTO() {}

    public RegisterRequestDTO(String CF, String nome, String cognome, String email) {
        this.CF = CF;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public String getCF() {
        return CF;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
