package it.unimib.sd2025.model;

import java.time.Instant;
import java.util.Date;

public class Buono {
    private int id;
    private int type;
    private float value;
    private boolean consumed;
    private Date creation;
    private Date modified;

    public Buono(int id, float importo, int tipologia, boolean consumato, Date data_creazione,
            Date data_consumazione) {
        this.id = id;
        this.value = importo;
        this.type = tipologia;
        this.consumed = consumato;
        this.creation = data_creazione;
        this.modified = data_consumazione;
    }

    public int getId() {
        return id;
    }

    public static Buono build(String[] parameters) {
        if (parameters.length != 6) throw new RuntimeException("Buono::build : Invalid number of parameters");
        return new Buono(
            Integer.parseInt(parameters[0]), // ID
            Float.parseFloat(parameters[1]), // Valore
            Integer.parseInt(parameters[2]), // Tipo
            Boolean.parseBoolean(parameters[3]), // E' consumato
            Date.from(Instant.ofEpochMilli(Long.parseLong(parameters[4]))), // Data creazione
            Date.from(Instant.ofEpochMilli(Long.parseLong(parameters[5])))); // Data modifica
    }

    public void setId(int id) {
        this.id = id;
    }

    /*
     * public enum Tipologia {
     * CINEMA,
     * MUSICA,
     * CONCERTI,
     * EVENTI_CULTURALI,
     * LIBRI,
     * MUSEI,
     * STRUMENTI_MUSICALI,
     * TEATRO,
     * DANZA
     * }
     */

    public void consuma() {
        consumed = true;
        modified = new Date();
    }

    public float getValue() {
        return value;
    }

    public void setValue(float importo) {
        this.value = importo;
    }

    public int getType() {
        return type;
    }

    public void setType(int tipologia) {
        this.type = tipologia;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumato) {
        this.consumed = consumato;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date data_creazione) {
        this.creation = data_creazione;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date data_consumazione) {
        this.modified = data_consumazione;
    }

    public String[] TCPost() {
        return new String[] {};
    }

}
