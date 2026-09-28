package it.unimib.sd2025.model;

public enum UserQueries implements Queries {
    NOME("nome"),
    COGNOME("cognome"),
    SALDO("saldo"),
    TRATTENUTO("held"),
    EMAIL("email"),
    CF("cf"),
    LAST("last"),
    ;

    private String query;

    UserQueries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public static UserQueries fromString(String query) {
        for (UserQueries q : UserQueries.values()) {
            if (q.getQuery().equals(query)) {
                return q;
            }
        }
        throw new IllegalArgumentException("Invalid query: " + query);
    }
}
