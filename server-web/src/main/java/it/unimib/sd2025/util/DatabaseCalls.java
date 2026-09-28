package it.unimib.sd2025.util;

import java.io.IOException;
import java.util.function.Function;

import it.unimib.sd2025.DatabaseConnection;
import it.unimib.sd2025.DatabaseConnection.DatabaseRequest.RequestOperation;
import it.unimib.sd2025.DatabaseConnection.DatabaseResponse.ResponseOperation;
import it.unimib.sd2025.handler.common.EndpointHandler;
import it.unimib.sd2025.handler.common.ResourceBuilder;
import it.unimib.sd2025.model.Queries;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import static it.unimib.sd2025.DatabaseConnection.DATABASE_HOST;
import static it.unimib.sd2025.DatabaseConnection.DATABASE_PORT;

public class DatabaseCalls {
    // Attributi per connessione al database


    /**
     * Finalizza l'operazione di risposta, wrappando il contenuto della risposta nel
     * database, formattata in json
     * tramite il callback, e ritornando la giusta Response
     * 
     * @param response           la risposta del database
     * @param DTOClass la classe per fare il parsing del
     *                           contenuto della risposta
     * @return una Response class costruita in base al codice della DatabaseResponse
     */
    public static <T> Response completeStdResponse(DatabaseConnection.DatabaseResponse response,
            ResourceBuilder<T> DTOClass) {
        return switch (response.getCode()) {
            case ResponseOperation.OK ->
                Response.ok(DTOClass.build(response.getContent()), MediaType.APPLICATION_JSON).build();
            case ResponseOperation.ERROR -> Response.status(Status.INTERNAL_SERVER_ERROR).build();
            case ResponseOperation.NOT_FOUND -> Response.status(Status.NOT_FOUND).build();
            case ResponseOperation.QUIT -> Response.ok().build();
            default ->
                throw new IllegalArgumentException(String.format("%s è un'operazione non valida", response.getCode()));
        };
    }
    public static Response completeStdResponse(DatabaseConnection.DatabaseResponse response) {
        return switch (response.getCode()) {
            case ResponseOperation.OK, ResponseOperation.QUIT ->
                    Response.ok().build();
            case ResponseOperation.ERROR -> Response.status(Status.INTERNAL_SERVER_ERROR).build();
            case ResponseOperation.NOT_FOUND -> Response.status(Status.NOT_FOUND).build();
            default ->
                    throw new IllegalArgumentException(String.format("%s è un'operazione non valida", response.getCode()));
        };
    }

    /**
     * Rappresenta la chiamata di default al database
     * 
     * @param reqOp               il tipo di richiesta fatta al database
     * @param authID              l'id dello user richiesto
     * @param DTOClass la classe delle informazioni richieste
     * @param formattedParameters il parametro aggiuntivo (opzionale), che specifica
     *                            la richiesta
     * @return una risposta in JSON formattata, dipendente dal content della
     *         risposta del db.
     */
    public static <T> Response defaultCall(RequestOperation reqOp, String authID, ResourceBuilder<T> DTOClass,
            String... formattedParameters) {
        try (
                DatabaseConnection db_conn = new DatabaseConnection(DATABASE_HOST, DATABASE_PORT)) {
            var res = db_conn.send(reqOp, authID, formattedParameters);

            return completeStdResponse(res, DTOClass);
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    public static <T> Response defaultCall(RequestOperation reqOp, String authID, ResourceBuilder<T> DTOClass,
    Queries... queries) {
        String[] formattedParameters = new String[queries.length];
        for (int i = 0; i < queries.length; i++) {
            formattedParameters[i] = queries[i].getQuery();
        }
        return defaultCall(reqOp, authID, DTOClass, formattedParameters);
    }

    /**
     * Rappresenta il metodo di default per formattare i dati restituiti dal
     * database in una stringa JSON
     * 
     * @param to_format la stringa da formattare
     * @param keys      le varie chiavi che comporranno, in ordine, il json
     * @return una stringa JSON
     */
    public static String defaultFormatJson(String to_format, String... keys) { // TODO: remove?
        var values = to_format.split(String.valueOf(DatabaseConnection.DEF_SEPARATOR));
        if (keys.length > values.length)
            throw new RuntimeException("keys and values do not match");
        if (keys.length != values.length)
            System.err.printf("Attenzione! verranno scartati %d oggetti.\n", values.length - keys.length);
        StringBuilder s = new StringBuilder("{");
        // TODO: controlla il tipo dei valori, perché non devi inserire '"'
        for (int i = 0; i < keys.length; i++) {
            s.append("\"").append(keys[i]).append("\":\"").append(values[i]).append("\"");
            if (i < keys.length - 1)
                s.append(",");
        }
        s.append("}");
        return s.toString();
    }

}
