package it.unimib.sd2025.handler.modify;

/* Classe wrapper per poter passare a ModifyHandler.java il codice fiscale e l'id del buono contenuto nell'header della richiesta */
public class ModifyDTO {
    private final String CF;
    private final int IDBuono;
    private final ModifyRequestDTO modifyRequestDTO;

    public ModifyDTO(String CF, int IDBuono, ModifyRequestDTO modifyRequestDTO) {
        this.CF = CF;
        this.IDBuono = IDBuono;
        this.modifyRequestDTO = modifyRequestDTO;
    }

    public int getIDBuono() {
        return IDBuono;
    }

    public String getCF() {
        return CF;
    }

    public ModifyRequestDTO getModifyRequestDTO() {
        return modifyRequestDTO;
    }
}
