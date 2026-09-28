package it.unimib.sd2025.handler.modify;

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
import java.util.Arrays;
import java.util.Date;

import static it.unimib.sd2025.DatabaseConnection.DATABASE_HOST;
import static it.unimib.sd2025.DatabaseConnection.DATABASE_PORT;
import static it.unimib.sd2025.util.DatabaseCalls.completeStdResponse;
import static it.unimib.sd2025.util.Utilities.CFCheck;
import static it.unimib.sd2025.util.Utilities.kvbuilder;

public class ModifyHandler implements EndpointHandler<ModifyDTO> {
    @Override
    public Response handle(ModifyDTO modifyDTO) {
        /* TODO: Chiamata al database
         *
         * BISOGNA MODIFICARE LE VARIABILI GLOBALI
         * ContributiDisponibili = ContributiDisponibili + [Vecchio Valore Buono] -
         * [Nuovo Valore Buono]
         *
         * [SE IL BUONO **NON** VIENE CONSUMATO]
         * ContributiNonAncoraSpesi = ContributiNonAncoraSpesi + [Nuovo Valore Buono] -
         * [Vecchio Valore Buono]
         *
         * [SE IL BUONO VIENE CONSUMATO]
         * ContributiNonAncoraSpesi = ContributiNonAncoraSpesi - [Vecchio Valore Buono]
         * ContributiSpesi = ContributiSpesi + [Nuovo Valore Buono]
         * NumeroBuoniGeneratiNonConsumati--
         * NumeroBuoniGeneratiConsumati++
         *
         * [LISTA VARIABILI "GLOBALI"]
         * TotalUsers
         * ContributiDisponibili
         * ContributiNonAncoraSpesi
         * ContributiSpesi
         * NumeroBuoniGeneratiConsumati
         * NumeroBuoniGeneratiNonConsumati
         */

        // Nuovi valori
        var cf = modifyDTO.getCF();
        var bid = modifyDTO.getIDBuono();
        float val = Math.round(modifyDTO.getModifyRequestDTO().getValue() * 100f)/100f;
        var type = modifyDTO.getModifyRequestDTO().getType();
        var nconsumo = modifyDTO.getModifyRequestDTO().isConsumed();


        if (CFCheck(cf) || val < 0) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        try (
                DatabaseConnection db_conn = new DatabaseConnection(DATABASE_HOST, DATABASE_PORT)
        ) {
            //
            var ssend = db_conn.send(DatabaseConnection.DatabaseRequest.RequestOperation.GET, cf, UserQueries.SALDO);
            if (ssend.getCode() == DatabaseConnection.DatabaseResponse.ResponseOperation.ERROR) return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            if (ssend.getCode() == DatabaseConnection.DatabaseResponse.ResponseOperation.NOT_FOUND) return Response.status(Status.NOT_FOUND).build();
            float saldo = Float.parseFloat(ssend.getContent()[0]);
            var vsend = db_conn.send(
                DatabaseConnection.DatabaseRequest.RequestOperation.GET,
                "%s%c%d".formatted(cf, DatabaseConnection.ID_SEPARATOR, bid), 
                BuonoQueries.VALORE, // Indice 0
                BuonoQueries.IS_CONSUMATO); // Indice 1
            
            if (vsend.getCode() == DatabaseConnection.DatabaseResponse.ResponseOperation.ERROR) return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            if (vsend.getCode() == DatabaseConnection.DatabaseResponse.ResponseOperation.NOT_FOUND) return Response.status(Status.NOT_FOUND).build();
            float vval = Float.parseFloat(vsend.getContent()[0]);
            boolean vconsumo = Boolean.parseBoolean(vsend.getContent()[1]);

            // Checks
            if (vconsumo) return Response.status(Status.FORBIDDEN).entity("Un buono consumato non può essere modificato").build();
            if (val > saldo+vval) return Response.status(Status.FORBIDDEN).build();
            DatabaseConnection.DatabaseResponse bmod;
            if (val != 0) {
                bmod = db_conn.send(DatabaseConnection.DatabaseRequest.RequestOperation.MODIFY,
                        "%s%c%d".formatted(cf, DatabaseConnection.ID_SEPARATOR, bid),
                        kvbuilder(BuonoQueries.VALORE, val),
                        kvbuilder(BuonoQueries.IS_CONSUMATO, nconsumo),
                        kvbuilder(BuonoQueries.WHEN_MODIFICA, String.valueOf(new Date().getTime())),
                        kvbuilder(BuonoQueries.TIPO, type)
                );


            } else {
                bmod = db_conn.send(DatabaseConnection.DatabaseRequest.RequestOperation.DELETE,
                        "%s%c%d".formatted(cf, DatabaseConnection.ID_SEPARATOR, bid),
                        BuonoQueries.VALORE,
                        BuonoQueries.IS_CONSUMATO,
                        BuonoQueries.CREAZIONE,
                        BuonoQueries.TIPO,
                        BuonoQueries.WHEN_MODIFICA
                );
            }
            if (bmod.getCode() != DatabaseConnection.DatabaseResponse.ResponseOperation.OK)
                return completeStdResponse(bmod);
            DatabaseConnection.DatabaseResponse generic_modify;
            DatabaseConnection.DatabaseResponse user_modify;

            /* TODO: Mancano modifiche al saldo dell'utente */

            if (!nconsumo) {
                generic_modify = db_conn.send(
                        DatabaseConnection.DatabaseRequest.RequestOperation.INCR,
                        GenericQueries.getID(),
                        kvbuilder(GenericQueries.FREE, vval - val),
                        kvbuilder(GenericQueries.HELD, val - vval),
                        kvbuilder(GenericQueries.NBN, val == 0 ? -1 : 0)
                );
                user_modify = db_conn.send(
                        DatabaseConnection.DatabaseRequest.RequestOperation.INCR,
                        cf,
                        kvbuilder(UserQueries.SALDO, vval-val),
                        kvbuilder(UserQueries.TRATTENUTO, val-vval)
                );
            } else {
                generic_modify = db_conn.send(DatabaseConnection.DatabaseRequest.RequestOperation.INCR, GenericQueries.getID(),
                        kvbuilder(GenericQueries.FREE, vval-val),
                        kvbuilder(GenericQueries.HELD, -vval),
                        kvbuilder(GenericQueries.SOLD, val),
                        kvbuilder(GenericQueries.NBC, val == 0 ? 0 : 1),
                        kvbuilder(GenericQueries.NBN, -1));
                user_modify = db_conn.send(
                        DatabaseConnection.DatabaseRequest.RequestOperation.INCR,
                        cf,
                        kvbuilder(UserQueries.SALDO, vval-val),
                        kvbuilder(UserQueries.TRATTENUTO, -vval)
                );
            }
            if (user_modify.getCode() != DatabaseConnection.DatabaseResponse.ResponseOperation.OK) return completeStdResponse(user_modify);
            return completeStdResponse(generic_modify);


        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
