package it.unimib.sd2025;

import java.io.IOException;

public interface IDatabase {

    // Recupera valore/i
    String [] get(String ID, String... args) throws IOException;

    // Aggiorna una chiave con valore
    boolean set(String ID, String... args) throws IOException;

    // Elimina chiave/i
    boolean delete(String ID, String ... keys) throws IOException;

    // Aggiunge una chiave con valori
    void add(String ID, String... args) throws IOException;

    // Incrementa argomenti con valori
    boolean increment(String ID, String ... args) throws IOException;
}
