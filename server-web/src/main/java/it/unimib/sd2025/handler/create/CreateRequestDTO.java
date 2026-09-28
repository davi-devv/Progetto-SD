package it.unimib.sd2025.handler.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CreateRequestDTO {
    /*
     * Si utilizzano le annotazioni di validazione di Java EE per validare il tipo,
     * il server ritorna in automatico 400 BAD REQUEST in caso la richiesta non
     * rispetti
     * questi parametri
     */

    @Min(0)
    @Max(500)
    private float value;

    @Min(0)
    @Max(8)
    private int type;

    public CreateRequestDTO() {
    }

    public CreateRequestDTO(float value, int type) {
        this.value = value;
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
