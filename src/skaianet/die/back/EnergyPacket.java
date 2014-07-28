package skaianet.die.back;

public class EnergyPacket {
    private final double joules;
    private final EnergyPacket parent;

    public EnergyPacket(double joules, EnergyPacket parent) {
        this.joules = joules;
        this.parent = parent;
    }

    public String toString() {
        return "<" + joules + ":" + parent + ">";
    }
}
