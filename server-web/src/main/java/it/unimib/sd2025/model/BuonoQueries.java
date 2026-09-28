package it.unimib.sd2025.model;

public enum BuonoQueries implements Queries {
    TIPO("tipo"),
    VALORE("valore"),
    IS_CONSUMATO("consumato"),
    CREAZIONE("creazione"),
    WHEN_MODIFICA("modifica");

    private String query;

    BuonoQueries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public static BuonoQueries fromString(String query) {
        for (BuonoQueries q : BuonoQueries.values()) {
            if (q.getQuery().equals(query)) {
                return q;
            }
        }
        throw new IllegalArgumentException("Invalid query: " + query);
    }
}
