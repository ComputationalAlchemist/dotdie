package skaianet.die.standalone;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;
import skaianet.die.front.ColoredIdentifier;

public class Universe implements ATHAlive {
    @ATHcessible
    private final ColoredIdentifier name;

    public Universe(ColoredIdentifier name) {
        this.name = name;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public double getEnergy() {
        return 4e69; // Total energy in the universe, in Joules.
    }

    public String toString() {
        return "Universe " + name;
    }
}
