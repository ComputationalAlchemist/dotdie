package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.back.LoopContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class EnterLoopInstruction extends Instruction {
    private final int object;
    private Integer label = null;

    public EnterLoopInstruction(Color thread, int object) {
        super(thread);
        this.object = object;
    }

    public void bind(int label) {
        if (this.label != null) {
            throw new IllegalStateException("Branch already bound!");
        }
        this.label = label;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("ENTER " + object + " -> " + label);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        LoopContext localContext = executionContext.loopContext(executionContext.get(object));
        executionContext.put(object, localContext);
        if (!localContext.setupLoop()) {
            executionContext.jump(label);
        }
    }
}
