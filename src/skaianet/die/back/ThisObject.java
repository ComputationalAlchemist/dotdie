package skaianet.die.back;

import skaianet.die.front.Color;

public class ThisObject {
    private final ExecutionContext context;

    public ThisObject(ExecutionContext context) {
        this.context = context;
    }

    @ATHcessible
    public void DIE() {
        context.programRequestedTerminate();
    }

    public String toString() {
        Color thread = context.getRootColor();
        return "THIS[" + (thread == null ? "????" : thread) + "]";
    }
}
