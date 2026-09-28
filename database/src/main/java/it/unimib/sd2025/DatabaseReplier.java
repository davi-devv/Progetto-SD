package it.unimib.sd2025;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

// Classe che implementa la connessione con il database
public class DatabaseReplier {
    public static final int DATABASE_PORT = 3890;
    private final IDatabase database;

    public DatabaseReplier(IDatabase database) throws IOException {
        this.database = database;
    }
    public static final char KV_SEPARATOR = ':';
    public static final char ID_SEPARATOR = '_';
    public static final char DEF_SEPARATOR = ';';
    /*
     * STRUTTURA MESSAGGI DATABASE:
     * OP_CODE-CF-MESSAGE
     */

    // Classe utilizzata per gestire i messaggi in richiesta
    public static class DatabaseRequest {
        public enum RequestOperation {
            GET("GET"),
            SET("SET"),
            DELETE("DEL"),
            ADD("ADD"),
            INCREMENT("INCR"),
            QUIT("QUIT");

            private final String code;

            RequestOperation(String code) {
                this.code = code;
            }

            public String getCode() {
                return code;
            }

            public static RequestOperation get(String code) {
                for (RequestOperation op : RequestOperation.values()) {
                    if (op.getCode().equals(code)) {
                        return op;
                    }
                }
                throw new IllegalArgumentException("Operazione Sconosciuta: " + code);
            }
        }

        private final RequestOperation op;
        private final String[] payload;
        private final String authID;
        public DatabaseRequest(String message) {

            /* DESANITIZE */
            int spl_count = 1;
            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                if (c == '\\') {
                    i++;
                } else if (c == DEF_SEPARATOR) {
                    spl_count++;
                }
            }
            String[] rawData = new String[spl_count];
            boolean skip = false;
            for (int i = 0; i < message.length(); i++) {
                if (rawData[rawData.length-spl_count] == null) {
                    rawData[rawData.length-spl_count] = "";
                }
                char c = message.charAt(i);
                if (c == '\\' && !skip) {
                    skip = true;
                } else if (c == DEF_SEPARATOR && !skip) {
                    spl_count--;
                } else {
                    skip = false;
                    rawData[rawData.length-spl_count] += c;
                }

            }
            assert spl_count == 0;


            this.op = RequestOperation.get(rawData[0]);

            // Se l'array rawData è piu lungo di 1, allora authID è il secondo elemento.
            // Altrimenti è null.
            this.authID = (rawData.length > 1) ? rawData[1] : null;
            this.payload = (rawData.length > 2) ? Arrays.copyOfRange(rawData, 2, rawData.length) : null;
        }

        public String[] getPayload() {
            return payload;
        }

        public RequestOperation getOp() {
            return op;
        }

        public String getAuthID() {
            return authID;
        }
    }

    public static class DatabaseResponse {
        public enum ResponseOperation {
            OK("OK"),
            NOT_FOUND("NOT_FOUND"),
            QUIT("QUIT"),
            ERROR("ERROR");

            private final String code;

            public String getCode() {
                return code;
            }

            ResponseOperation(String code) {
                this.code = code;
            }

        }

        private final ResponseOperation code;
        private final String content;

        public DatabaseResponse(ResponseOperation code, String... content) {
            this.code = code;

            this.content = String.join(String.valueOf(DEF_SEPARATOR), content);
        }

        public ResponseOperation getCode() {
            return code;
        }

        public String formatMessage() {
            return String.format("%s%c%s", code.getCode(), DEF_SEPARATOR, content);
        }

    }

    public DatabaseResponse reply(DatabaseRequest request) throws IllegalArgumentException, IOException {
        switch (request.getOp()) {
            case GET -> {
                if (request.getAuthID() == null) {
                    throw new IllegalArgumentException("Richiesta GET senza AuthID");
                }

                if (request.getPayload() == null)
                    throw new IllegalArgumentException("Richiesta GET senza payload");

                var get_elem = database.get(request.getAuthID(), request.getPayload());
                for (String s : get_elem) {
                    if (s == null) {
                        return new DatabaseResponse(DatabaseResponse.ResponseOperation.NOT_FOUND);
                    }
                }
                return new DatabaseResponse(DatabaseResponse.ResponseOperation.OK, get_elem);
            }
            case ADD -> {
                if (request.getPayload() == null)
                    throw new IllegalArgumentException("Richiesta ADD senza payload");

                database.add(request.getAuthID(), request.getPayload());
                return new DatabaseResponse(DatabaseResponse.ResponseOperation.OK);
            }
            case SET -> {
                if (request.getPayload() == null)
                    throw new IllegalArgumentException("Richiesta SET senza payload");

                if (database.set(request.getAuthID(), request.getPayload()))
                    return new DatabaseResponse(DatabaseResponse.ResponseOperation.OK);

                return new DatabaseResponse(DatabaseResponse.ResponseOperation.NOT_FOUND);

            }
            case DELETE -> {
                if (request.getPayload() == null)
                    throw new IllegalArgumentException("Richiesta DELETE senza payload");

                if (database.delete(request.getAuthID(), request.getPayload()))
                    return new DatabaseResponse(DatabaseResponse.ResponseOperation.OK);
                return new DatabaseResponse(DatabaseResponse.ResponseOperation.NOT_FOUND);

            }
            case INCREMENT -> {
                if (request.getPayload() == null)
                    throw new IllegalArgumentException("Richiesta INCREMENT senza payload");

                if (database.increment(request.getAuthID(), request.getPayload()))
                    return new DatabaseResponse(DatabaseResponse.ResponseOperation.OK);
                return new DatabaseResponse(DatabaseResponse.ResponseOperation.NOT_FOUND);
            }

            case QUIT -> {
                if (request.getAuthID().equals("AUTH::KEY")) {
                    return new DatabaseResponse(DatabaseResponse.ResponseOperation.QUIT);
                } else {
                    return new DatabaseResponse(DatabaseResponse.ResponseOperation.ERROR, "Wrong auth key for quitting");
                }
            }
        }
        return new DatabaseResponse(DatabaseResponse.ResponseOperation.ERROR, "Operation not found");
    }

    public DatabaseResponse reply(String msg) throws IOException {

        DatabaseRequest request = null;
        DatabaseResponse response = null;

        try {
            System.out.println("Raw message: "+msg);
            request = new DatabaseRequest(msg);
            response = reply(request);

        } catch (IllegalArgumentException e) {
            response = new DatabaseResponse(DatabaseResponse.ResponseOperation.ERROR, "Argomento illegale: "+e.getMessage());

        } catch (Exception e) {
            response = new DatabaseResponse(DatabaseResponse.ResponseOperation.ERROR, "Errore generico: "+e.getMessage());
        }

        return response;
    }

}

