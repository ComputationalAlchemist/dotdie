package skaianet.die.standalone;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;

import java.util.concurrent.atomic.AtomicInteger;

class Dream implements ATHAlive {
    private final AtomicInteger ctr;
    private final int length;

    public Dream(int length) {
        this.length = length;
        ctr = new AtomicInteger();
    }

    @ATHcessible
    public void sleep() throws InterruptedException {
        ctr.incrementAndGet();
        try {
            Thread.sleep(length);
        } finally {
            ctr.decrementAndGet();
        }
    }

    @Override
    public boolean isAlive() {
        return ctr.get() > 0;
    }

    @Override
    public double getEnergy() {
        return 0;
    }
}
