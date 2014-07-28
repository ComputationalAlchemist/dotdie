package skaianet.die.standalone;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;

public class Bit implements ATHAlive {
    private boolean value = false;

    @ATHcessible
    public void bind(boolean value) {
        this.value = value;
    }

    @Override
    public boolean isAlive() {
        return value;
    }

    @Override
    public double getEnergy() {
        return 0;
    }
}
