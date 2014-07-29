package skaianet.die.back;

public class ThisObject {
    private final ExecutionContext context;

    public ThisObject(ExecutionContext context) {
        this.context = context;
    }

    @ATHcessible
    public void DIE() {
        context.programRequestedTerminate();
    }
}
