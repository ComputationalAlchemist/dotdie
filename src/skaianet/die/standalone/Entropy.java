package skaianet.die.standalone;

import skaianet.die.back.ATHcessible;

import java.util.Random;

public class Entropy {
    private final Random random = new Random();

    @ATHcessible
    public int next(int limit) {
        return random.nextInt(limit);
    }

    @ATHcessible
    public int next() {
        return random.nextInt();
    }
}
