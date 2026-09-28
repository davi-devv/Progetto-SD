package it.unimib.sd2025.handler.login;

import it.unimib.sd2025.DatabaseConnection;
import it.unimib.sd2025.handler.common.EndpointHandler;
import it.unimib.sd2025.model.UserQueries;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import static it.unimib.sd2025.util.DatabaseCalls.defaultCall;
import static it.unimib.sd2025.util.Utilities.CFCheck;

public class LoginHandler implements EndpointHandler<String> {

    @Override
    public Response handle(String CF) {
        if (CFCheck(CF)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        return defaultCall(DatabaseConnection.DatabaseRequest.RequestOperation.GET, CF, LoginDTO::build, UserQueries.NOME, UserQueries.COGNOME, UserQueries.EMAIL);

    }
}
