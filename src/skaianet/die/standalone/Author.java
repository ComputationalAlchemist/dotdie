package skaianet.die.standalone;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;
import skaianet.die.front.ColoredIdentifier;

public class Author implements ATHAlive {
    @ATHcessible
    private final ColoredIdentifier name;

    public Author(ColoredIdentifier name) {
        this.name = name;
    }

    @Override
    public boolean isAlive() {
        return true; // Program must assume that the author is alive.
    }

    @Override
    public double getEnergy() {
        return 6e18; // Estimated mass-energy of a human, in Joules.
    }

    public String toString() {
        return "Author " + name;
    }
}
