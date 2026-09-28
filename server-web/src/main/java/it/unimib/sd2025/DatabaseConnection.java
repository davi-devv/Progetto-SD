package it.unimib.sd2025;

import it.unimib.sd2025.model.Queries;
import it.unimib.sd2025.util.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

// Classe che implementa la connessione con il database
public class DatabaseConnection implements AutoCloseable {
    public static final String DATABASE_HOST = "localhost";
    public static final int DATABASE_PORT = 3890;

    private final Socket socket;
    private final PrintWriter out; // va al db
    private final BufferedReader in; // riceve dal db
    public DatabaseConnection(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static final char KV_SEPARATOR = ':';
    public static final char DEF_SEPARATOR = ';';
    public static final char ID_SEPARATOR = '_';
    /*
    * STRUTTURA MESSAGGI DATABASE:
    * OP_CODE-MESSAGE
    * */

    // Classe utilizzata per gestire i messaggi in richiesta
    public static class DatabaseRequest {
        public enum RequestOperation {
            GET("GET"),
            ADD("ADD"),
            MODIFY("SET"),
            DELETE("DEL"),
            INCR("INCR"),
            QUIT("QUIT");

            private final String code;

            RequestOperation(String code) {
                this.code = code;
            }
            public String getCode() {
                return code;
            }
        }
        private final RequestOperation op;
        private final String[] parameters;
        private final String authID;
        public DatabaseRequest(RequestOperation op, String authID, String... parameters) {
            this.op = op;
            this.parameters = parameters;
            this.authID = authID;
        }
        public String formatMessage() {
            return String.format("%s%c%s%c%s", op.getCode(), DEF_SEPARATOR, authID, DEF_SEPARATOR, String.join(String.valueOf(DEF_SEPARATOR), parameters));
        }
    }

    public static class DatabaseResponse {
        public enum ResponseOperation {
            OK("OK"),
            NOT_FOUND("NOT_FOUND"),
            ERROR("ERROR"),
            QUIT("QUIT");


            private final String code;

            public String getCode() {
                return code;
            }
            ResponseOperation(String code) {
                this.code = code;
            }
            public static ResponseOperation get(String code) {
                for (ResponseOperation op : ResponseOperation.values()) {
                    if (op.getCode().equals(code)) {
                        return op;
                    }
                }
                throw new IllegalArgumentException("Unknown operation: " + code);
            }
        }
        private final ResponseOperation code;
        private final String[] content;
        public DatabaseResponse(String message) {
            String[] rawData = message.split(String.valueOf(DEF_SEPARATOR));
            code = ResponseOperation.get(rawData[0]);
            content = Arrays.copyOfRange(rawData, 1, rawData.length);
        }
        private DatabaseResponse(ResponseOperation code, String... content) {
            this.code = code;
            this.content = content;
        }
        public ResponseOperation getCode() {
            return code;
        }
        public String[] getContent() {
            return content;
        }
    }

    public DatabaseResponse send(DatabaseRequest request) throws IOException {
        out.println(request.formatMessage());
        System.out.println("Raw message: "+request.formatMessage());
        String rawResponse = in.readLine();
        System.out.println("Raw response: "+rawResponse);

        if (rawResponse == null) {
            return new DatabaseResponse(DatabaseResponse.ResponseOperation.ERROR, "No response");
        }
        return new DatabaseResponse(rawResponse);
    }
    public DatabaseResponse send(DatabaseRequest.RequestOperation op, String authID, String... parameters) throws IOException {
        return send(new DatabaseRequest(op, authID, parameters));
    }
    public DatabaseResponse send(DatabaseRequest.RequestOperation op, String authID) throws IOException {
        return send(new DatabaseRequest(op, authID));
    }

    public DatabaseResponse send(DatabaseRequest.RequestOperation op, String authID, Queries... queries) throws IOException {
        String[] parameters = new String[queries.length];
        for (int i = 0; i < queries.length; i++) {
            parameters[i] = queries[i].getQuery();
        }

        return send(new DatabaseRequest(op, authID, parameters));
    }



    @Override
    public void close() throws IOException {
        // Chiudi prima gli stream, poi il socket
        IOException exception = null;

        var res = send(DatabaseRequest.RequestOperation.QUIT, "AUTH::KEY");

        if (res.getCode() == DatabaseResponse.ResponseOperation.ERROR) throw new IOException(res.getContent()[0]);

        try {
            if (out != null) out.close(); // PrintWriter.close() non lancia IOException
        } catch (Exception e) {
            exception = new IOException("Errore durante la chiusura di PrintWriter", e);
        }

        try {
            if (in != null) in.close();
        } catch (IOException e) {
            if (exception == null) exception = e;
        }

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            if (exception == null) exception = e;
        }

        if (exception != null) throw exception;
    }
}
