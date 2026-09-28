package it.unimib.sd2025.model;

public enum Types {
    CINEMA(0),
    MUSICA(1),
    CONCERTI(2),
    EVENTI_CULTURALI(3),
    LIBRI(4),
    MUSEI(5),
    STRUMENTI_MUSICALI(6),
    TEATRO(7),
    DANZA(8);

    private int id;

    Types(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Types fromId(int id) {
        for (Types t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        throw new IllegalArgumentException("ID non valido: " + id);
    }
}
