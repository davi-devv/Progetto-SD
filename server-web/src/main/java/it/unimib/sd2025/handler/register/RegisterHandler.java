package it.unimib.sd2025.handler.register;

import it.unimib.sd2025.DatabaseConnection;
import it.unimib.sd2025.handler.common.EndpointHandler;
import it.unimib.sd2025.model.GenericQueries;
import it.unimib.sd2025.model.User;
import it.unimib.sd2025.model.UserQueries;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.io.IOException;

import static it.unimib.sd2025.DatabaseConnection.DATABASE_HOST;
import static it.unimib.sd2025.DatabaseConnection.DATABASE_PORT;
import static it.unimib.sd2025.util.DatabaseCalls.completeStdResponse;
import static it.unimib.sd2025.util.Utilities.kvbuilder;

public class RegisterHandler implements EndpointHandler<RegisterRequestDTO> {

    @Override
    public Response handle(RegisterRequestDTO requestBody) {
        /*
         *
         * [Inizializzazione User]
         * SET-CF-nome:requestBody.nome
         * SET-CF-cognome:requestBody.cognome
         * SET-CF-email:requestBody.email
         * SET-CF-freeBalance:500
         * SET-CF-usedNotConsumed:0
         * SET-CF-lastID:0 --> Nessun buono creato
         * [Fine Inizializzazione User]
         *
         * [Variabili Globali Database]
         * totalUsers = GET-TotalUsers
         * SET-TotalUsers++
         *
         * contributiDisponibili = GET-ContributiDisponibili
         * SET-ContributiDisponibili + 500
         * [Fine Variabili Globali]
         *
         * Forse manca qualcosa per inizializzare un utente nel database, da guardare
         * Possibilmente creiamo una funzione che inizializzi un utente in User.java (?)
         */

        /* Non c'è bisogno di fare un return per il codice 400 BAD REQUEST perchè il binder JSON ritorna già un codice 400 quando non riesce a serializzare il DTO */

        var cf = requestBody.getCF();
        try (
                DatabaseConnection db_conn = new DatabaseConnection(DATABASE_HOST, DATABASE_PORT)
        ) {
            var check = db_conn.send(DatabaseConnection.DatabaseRequest.RequestOperation.GET, cf, UserQueries.NOME);
            if (check.getCode() == DatabaseConnection.DatabaseResponse.ResponseOperation.OK)
                return Response.status(Status.CONFLICT).entity("User already exists").build();
            if (check.getCode() == DatabaseConnection.DatabaseResponse.ResponseOperation.ERROR)
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            var add = db_conn.send(DatabaseConnection.DatabaseRequest.RequestOperation.ADD, cf,
                    kvbuilder(UserQueries.NOME, requestBody.getNome()),
                    kvbuilder(UserQueries.COGNOME, requestBody.getCognome()),
                    kvbuilder(UserQueries.EMAIL, requestBody.getEmail()),
                    kvbuilder(UserQueries.SALDO, 500),
                    kvbuilder(UserQueries.LAST, 0),
                    kvbuilder(UserQueries.TRATTENUTO, 0));
            if (add.getCode() != DatabaseConnection.DatabaseResponse.ResponseOperation.OK) {
                return completeStdResponse(add);
            }
            System.out.println(add.getCode());

            var genr = db_conn.send(DatabaseConnection.DatabaseRequest.RequestOperation.INCR, GenericQueries.getID(),
                    kvbuilder(GenericQueries.UTENTI, 1),
                    kvbuilder(GenericQueries.FREE, 500));
            System.out.println(genr.getCode());
            return completeStdResponse(genr);
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
