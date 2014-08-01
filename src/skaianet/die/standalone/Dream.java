package skaianet.die.standalone;

import skaianet.die.back.ATHcessible;
import skaianet.die.back.ExecutionContext;

public class Dream {
    @ATHcessible
    public int length;

    public Dream(int length) {
        this.length = length;
    }

    @ATHcessible
    public void sleep(ExecutionContext context) throws InterruptedException {
        context.suspend(length);
    }
}
