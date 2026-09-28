package it.unimib.sd2025.handler.modify;

public class ModifyRequestDTO {
    private int type;
    private float value;
    private boolean consumed;

    // Costruttore vuoto richiesto da JsonBuilder
    public ModifyRequestDTO() {}

    public ModifyRequestDTO(int type, float value, boolean consumed) {
        this.type = type;
        this.value = value;
        this.consumed = consumed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }
}
