package it.unimib.sd2025.model;

public enum GenericQueries implements Queries {
    UTENTI("Utenti"),
    FREE("Free"),
    HELD("Held"),
    SOLD("Sold"),
    NBC("NBC"),
    NBN("NBN")
    ;

    private String query;

    GenericQueries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public static GenericQueries fromString(String query) {
        for (GenericQueries q : GenericQueries.values()) {
            if (q.getQuery().equals(query)) {
                return q;
            }
        }
        throw new IllegalArgumentException("Invalid query: " + query);
    }
    public static String getID() {
        return "System";
    }
}
