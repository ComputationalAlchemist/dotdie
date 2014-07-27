package skaianet.die.standalone;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;

/**
 * Created on 2014-07-26.
 */
public class Author implements ATHAlive {
    @ATHcessible
    public final String name;

    public Author(String name) {
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
}
