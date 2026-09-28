package it.unimib.sd2025.handler.balance;

import static it.unimib.sd2025.DatabaseConnection.DatabaseRequest.RequestOperation;
import static it.unimib.sd2025.util.DatabaseCalls.*;
import it.unimib.sd2025.handler.common.EndpointHandler;
import static it.unimib.sd2025.util.Utilities.CFCheck;

import it.unimib.sd2025.model.UserQueries;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class BalanceHandler implements EndpointHandler<String> {

    @Override
    public Response handle(String codiceFiscale) {
        if (CFCheck(codiceFiscale)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        return defaultCall(RequestOperation.GET, codiceFiscale, BalanceDTO::build, UserQueries.SALDO, UserQueries.TRATTENUTO);
    }
}
