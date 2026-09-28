package it.unimib.sd2025.handler.balance;

import it.unimib.sd2025.handler.common.ResourceBuilder;

public class BalanceDTO {
    private final float freeBalance;
    private final float usedNotConsumed;

    public BalanceDTO(float freeBalance, float usedNotConsumed) {
        this.freeBalance = freeBalance;
        this.usedNotConsumed = usedNotConsumed;
    }

    public float getFreeBalance() {
        return freeBalance;
    }

    public float getUsedNotConsumed() {
        return usedNotConsumed;
    }

    public static BalanceDTO build(String[] parsing) {
        if (parsing.length != 2) throw new RuntimeException("BalanceDTO::build : Invalid number of parameters");
        return new BalanceDTO(Float.parseFloat(parsing[0]), Float.parseFloat(parsing[1]));
    }
}
