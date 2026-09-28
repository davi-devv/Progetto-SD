package it.unimib.sd2025.handler.create;

import it.unimib.sd2025.DatabaseConnection;
import it.unimib.sd2025.handler.common.EndpointHandler;
import it.unimib.sd2025.model.BuonoQueries;
import it.unimib.sd2025.model.GenericQueries;
import it.unimib.sd2025.model.UserQueries;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static it.unimib.sd2025.util.DatabaseCalls.*;
import static it.unimib.sd2025.DatabaseConnection.DATABASE_HOST;
import static it.unimib.sd2025.DatabaseConnection.DATABASE_PORT;
import static it.unimib.sd2025.DatabaseConnection.DatabaseRequest.RequestOperation;
import static it.unimib.sd2025.DatabaseConnection.DatabaseResponse.ResponseOperation;
import static it.unimib.sd2025.util.Utilities.CFCheck;
import static it.unimib.sd2025.util.Utilities.kvbuilder;


public class CreateHandler implements EndpointHandler<CreateDTO> {

    @Override
    public Response handle(CreateDTO createDTO) {
        /*
         * lastID = GET-lastID
         * IDNuovoBuono = lastID++
         * SET-CF-lastID:IDNuovoBuono
         *
         * Bisogna sempre decidere come mettere in memoria le date
         * SET-CF_IDNuovoBuono-type:tipo-value:valore-consumed:false-creation:data-modified:data
         *
         * BISOGNA ANCHE CAMBIARE LE VARIABILI GLOBALI
         * ContributiDisponibili = ContributiDisponibili - [Valore nuovo buono]
         * ContributiNonAncoraSpesi = ContributiNonAncoraSpesi + [Valore nuovo buono]
         * NumeroBuoniGeneratiNonConsumati = NumeroBuoniGeneratiNonConsumati++
         *
         * Tutti i valori sono CASE SENSITIVE
         *
         * [LISTA VARIABILI "GLOBALI"]
         * TotalUsers
         * ContributiDisponibili
         * ContributiNonAncoraSpesi
         * ContributiSpesi
         * NumeroBuoniGeneratiConsumati
         * NumeroBuoniGeneratiNonConsumati
         */

        var cf = createDTO.getCF();
        float val = Math.round(createDTO.getCreateRequestDTO().getValue() * 100f)/100f;
        if (val < 0.01) {
            return Response.status(Status.BAD_REQUEST).entity("Il valore del buono non può essere meno di 0.01").build();
        }

        if (CFCheck(cf)) return Response.status(Status.UNAUTHORIZED).build();

        try (
                DatabaseConnection db_conn = new DatabaseConnection(DATABASE_HOST, DATABASE_PORT)
        ) {
            var res = db_conn.send(RequestOperation.GET, cf, UserQueries.SALDO);
            if (res.getCode() == ResponseOperation.ERROR) return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            if (res.getCode() == ResponseOperation.NOT_FOUND) return Response.status(Status.NOT_FOUND).build();
            float saldo = Float.parseFloat(res.getContent()[0]);

            if (val > saldo) return Response.status(Status.FORBIDDEN).build();

            var lidx_res = db_conn.send(RequestOperation.GET, cf, UserQueries.LAST);
            if (lidx_res.getCode() == ResponseOperation.ERROR) return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            if (lidx_res.getCode() == ResponseOperation.NOT_FOUND) return Response.status(Status.NOT_FOUND).build();
            int new_index = Integer.parseInt(lidx_res.getContent()[0])+1;


            /*
             * Il resto dei parametri sono generati dal server
             *
             * [NuovoID] = Come mostrato prima (lastID++)
             * [Consumed] = Alla creazione del buono è sempre false
             * [Creation] = Data e ora corrente
             * [Modified] = Sempre data e ora corrente, cambia solamente se il client manda una /modify
             */

            // Aggiunge il nuovo buono
            String now = String.valueOf(new Date().getTime());
            var sec_res = db_conn.send(RequestOperation.ADD, "%s%c%d".formatted(cf, DatabaseConnection.ID_SEPARATOR, new_index),
                    kvbuilder(BuonoQueries.VALORE, val),
                    kvbuilder(BuonoQueries.CREAZIONE, now),
                    kvbuilder(BuonoQueries.TIPO, createDTO.getCreateRequestDTO().getType()),
                    kvbuilder(BuonoQueries.IS_CONSUMATO, false),
                    kvbuilder(BuonoQueries.WHEN_MODIFICA, now));
            if (sec_res.getCode() != ResponseOperation.OK) {
                return completeStdResponse(sec_res);
            }

            // Modifica i dati generali
            var modify = db_conn.send(RequestOperation.INCR, GenericQueries.getID(),
                    kvbuilder(GenericQueries.FREE, -val),
                    kvbuilder(GenericQueries.HELD, val),
                    kvbuilder(GenericQueries.NBN, 1));
            if (modify.getCode() != ResponseOperation.OK) {
                return completeStdResponse(modify);
            }

            System.out.println(kvbuilder(UserQueries.SALDO, -val));
            var modified = db_conn.send(RequestOperation.INCR, cf,
                    kvbuilder(UserQueries.LAST, 1),
                    kvbuilder(UserQueries.SALDO, -val),
                    kvbuilder(UserQueries.TRATTENUTO, val));
            return completeStdResponse(modified);

        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }


    }
}
