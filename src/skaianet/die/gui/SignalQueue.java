package skaianet.die.gui;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;

import java.util.LinkedList;

public class SignalQueue implements ATHAlive {
    private final LinkedList<Object> signals = new LinkedList<>();

    @ATHcessible
    public void post(Object o) {
        if (o != null) {
            signals.addLast(o);
        }
    }

    @ATHcessible
    public Object pull() {
        return signals.isEmpty() ? null : signals.removeFirst();
    }

    @Override
    public boolean isAlive() {
        return !signals.isEmpty();
    }

    @Override
    public double getEnergy() {
        return 0;
    }
}
