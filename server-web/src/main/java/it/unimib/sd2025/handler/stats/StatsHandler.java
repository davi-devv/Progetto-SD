package it.unimib.sd2025.handler.stats;

import it.unimib.sd2025.DatabaseConnection;
import it.unimib.sd2025.handler.common.EndpointHandler;
import it.unimib.sd2025.model.GenericQueries;
import it.unimib.sd2025.model.UserQueries;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import static it.unimib.sd2025.util.DatabaseCalls.defaultCall;
import static it.unimib.sd2025.util.Utilities.CFCheck;

public class StatsHandler implements EndpointHandler<Void> {

    @Override
    public Response handle(Void input) {
        return defaultCall(DatabaseConnection.DatabaseRequest.RequestOperation.GET,
                GenericQueries.getID(), StatsDTO::build,
                GenericQueries.UTENTI,
                GenericQueries.FREE,
                GenericQueries.HELD,
                GenericQueries.SOLD,
                GenericQueries.NBN,
                GenericQueries.NBC
                );

    }
}
