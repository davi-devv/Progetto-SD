package it.unimib.sd2025.handler.stats;

public class StatsDTO {
    private final int totalUsers;
    private final float contributiDisponibili;
    private final float nonAncoraSpesi;
    private final float spesi;
    private final int buoniGeneratiConsumati;
    private final int buoniGeneratiNonConsumati;

    public StatsDTO(int totalUsers, float contributiDisponibili, float nonAncoraSpesi, float spesi,
            int buoniGeneratiConsumati, int buoniGeneratiNonConsumati) {
        this.totalUsers = totalUsers;
        this.contributiDisponibili = contributiDisponibili;
        this.nonAncoraSpesi = nonAncoraSpesi;
        this.spesi = spesi;
        this.buoniGeneratiConsumati = buoniGeneratiConsumati;
        this.buoniGeneratiNonConsumati = buoniGeneratiNonConsumati;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public float getContributiDisponibili() {
        return contributiDisponibili;
    }

    public float getNonAncoraSpesi() {
        return nonAncoraSpesi;
    }

    public float getSpesi() {
        return spesi;
    }

    public int getBuoniGeneratiConsumati() {
        return buoniGeneratiConsumati;
    }

    public int getBuoniGeneratiNonConsumati() {
        return buoniGeneratiNonConsumati;
    }

    public static StatsDTO build(String[] args) {
        if (args.length != 6) throw new RuntimeException("StatsDTO::build : Invalid number of parameters");
        return new StatsDTO(
                Integer.parseInt(args[0]),
                Float.parseFloat(args[1]),
                Float.parseFloat(args[2]),
                Float.parseFloat(args[3]),
                Integer.parseInt(args[4]),
                Integer.parseInt(args[5])
        );
    }
}
