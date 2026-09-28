package it.unimib.sd2025.handler.create;

/* Classe wrapper per poter passare a CreateHandler.java il codice fiscale contenuto nell'header della richiesta */
public class CreateDTO {
    private final String CF;
    private final CreateRequestDTO createRequestDTO;

    public CreateDTO(String cF, CreateRequestDTO createRequestDTO) {
        CF = cF;
        this.createRequestDTO = createRequestDTO;
    }

    public String getCF() {
        return CF;
    }

    public CreateRequestDTO getCreateRequestDTO() {
        return createRequestDTO;
    }
}
