package it.unimib.sd2025.handler.history;

import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2025.DatabaseConnection;
import it.unimib.sd2025.handler.common.EndpointHandler;
import it.unimib.sd2025.model.BuonoQueries;
import it.unimib.sd2025.model.UserQueries;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import it.unimib.sd2025.model.Buono;

import static it.unimib.sd2025.DatabaseConnection.DATABASE_HOST;
import static it.unimib.sd2025.DatabaseConnection.DATABASE_PORT;
import static it.unimib.sd2025.util.DatabaseCalls.completeStdResponse;
import static it.unimib.sd2025.util.Utilities.CFCheck;

public class HistoryHandler implements EndpointHandler<String> {

    @Override
    public Response handle(String codiceFiscale) {
        if (CFCheck(codiceFiscale)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        try (
                DatabaseConnection db_conn = new DatabaseConnection(DATABASE_HOST, DATABASE_PORT)
        ) {
            var lidx_res = db_conn.send(DatabaseConnection.DatabaseRequest.RequestOperation.GET, codiceFiscale, UserQueries.LAST);
            if (lidx_res.getCode() != DatabaseConnection.DatabaseResponse.ResponseOperation.OK) return completeStdResponse(lidx_res);

            int last_index = Integer.parseInt(lidx_res.getContent()[0]);

            /*
             * Servono tutti gli attributi dei buoni per tutti i buoni degli utenti, dunque
             * 
             * ultimoBuonoGenerato = GET-CF-lastID
             * FOR i < ultimoBuonoGenerato
             * GET-CF_i-valore-type-consumed-date1-date2
             */

            Map<Integer, Buono> listaBuoni = new HashMap<>();




            for (int i = 1; i <= last_index; i++) {
                var buono_reply = db_conn.send(
                        DatabaseConnection.DatabaseRequest.RequestOperation.GET,
                        "%s%c%d".formatted(codiceFiscale, DatabaseConnection.ID_SEPARATOR, i),
                        BuonoQueries.VALORE,
                        BuonoQueries.TIPO,
                        BuonoQueries.IS_CONSUMATO,
                        BuonoQueries.CREAZIONE,
                        BuonoQueries.WHEN_MODIFICA);

                if (buono_reply.getCode() == DatabaseConnection.DatabaseResponse.ResponseOperation.ERROR)
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();

                // Il costruttore di buono richiede anche l'ID all'inizio, di conseguenza
                // dobbiamo creare un nuovo array con anche il buono all'inizio
                if (buono_reply.getCode() == DatabaseConnection.DatabaseResponse.ResponseOperation.NOT_FOUND) continue;
                var updated = new String[buono_reply.getContent().length + 1];
                updated[0] = String.valueOf(i);
                System.arraycopy(buono_reply.getContent(), 0, updated, 1, buono_reply.getContent().length);


                Buono b = Buono.build(updated);
                listaBuoni.put(i, b);

            }

            HistoryDTO dto = new HistoryDTO(listaBuoni);
            return Response.ok(dto).build();

        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
