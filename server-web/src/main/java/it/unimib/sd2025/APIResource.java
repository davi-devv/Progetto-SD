package it.unimib.sd2025;

/* [JAKARTA] */
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/* [LOGIN] */
import it.unimib.sd2025.handler.login.LoginHandler;

/* [BALANCE] */
import it.unimib.sd2025.handler.balance.BalanceHandler;

/* [REGISTER] */
import it.unimib.sd2025.handler.register.RegisterHandler;
import it.unimib.sd2025.handler.register.RegisterRequestDTO;

/* [STATS] */
import it.unimib.sd2025.handler.stats.StatsHandler;

/* [HISTORY] */
import it.unimib.sd2025.handler.history.HistoryHandler;

/* [CREATE] */
import it.unimib.sd2025.handler.create.CreateHandler;
import it.unimib.sd2025.handler.create.CreateRequestDTO;
import it.unimib.sd2025.handler.create.CreateDTO;

/* [MODIFY] */
import it.unimib.sd2025.handler.modify.ModifyHandler;
import it.unimib.sd2025.handler.modify.ModifyRequestDTO;
import it.unimib.sd2025.handler.modify.ModifyDTO;

/**
 * Rappresenta la risorsa "api" in "http://localhost:8080/api"./balanc
 */
@Path("api")
public class APIResource {

    /*
     * [http://localhost:8080/api/login/{codiceFiscale}]
     */
    @Path("login/{codiceFiscale}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@PathParam("codiceFiscale") String codiceFiscale) {
        return new LoginHandler().handle(codiceFiscale);
    }

    /*
     * [http://localhost:8080/api/balance]
     */
    @Path("balance")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response balance(@HeaderParam("Authentication-ID") String authId) {
        return new BalanceHandler().handle(authId);
    }

    /*
     * [http://localhost:8080/api/register]
     */
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nuovoUtente(RegisterRequestDTO registerRequestDTO) {
        return new RegisterHandler().handle(registerRequestDTO);
    }

    /*
     * [http://localhost:8080/api/stats]
     */
    @Path("stats")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response stats() {
        return new StatsHandler().handle(null);
    }

    /*
     * [http://localhost:8080/api/history]
     */
    @Path("history")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response history(@HeaderParam("Authentication-ID") String authId) {
        return new HistoryHandler().handle(authId);
    }

    /*
     * [http://localhost:8080/api/create]
     */
    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nuovoBuono(@HeaderParam("Authentication-ID") String authId, CreateRequestDTO createRequestDTO) {

        /* Classe wrapper che unisce la richiesta e il CF. */
        CreateDTO createDTO = new CreateDTO(authId, createRequestDTO);

        return new CreateHandler().handle(createDTO);
    }

    /*
     * [http://localhost:8080/api/modify/{idBuono}]
     */
    @Path("modify/{idBuono}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nuovoBuono(@HeaderParam("Authentication-ID") String authId, @PathParam("idBuono") String idBuono,
            ModifyRequestDTO createRequestDTO) {

        /* Classe wrapper che unisce la richiesta, l'idBuono e il CF. */
        ModifyDTO modifyDTO = new ModifyDTO(authId, Integer.parseInt(idBuono), createRequestDTO);

        return new ModifyHandler().handle(modifyDTO);
    }
}