package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.back.LoopContext;

/**
 * Created on 2014-07-25.
 */
public class EnterLoopInstruction implements Instruction {
    private final int object;
    private Integer label = null;

    public EnterLoopInstruction(int object) {
        this.object = object;
    }

    public void bind(int label) {
        if (this.label != null) {
            throw new IllegalStateException("Branch already bound!");
        }
        this.label = label;
    }

    @Override
    public void print(int indent) {
        System.out.println("ENTER " + object + " -> " + label);
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
