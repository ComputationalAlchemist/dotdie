package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

/**
 * Created on 2014-07-25.
 */
public class InvokeInstruction implements Instruction {
    private final int target;
    private final int argumentCount;

    public InvokeInstruction(int target, int argumentCount) {
        this.target = target;
        this.argumentCount = argumentCount;
    }

    @Override
    public void print() {
        System.out.print("INVOKE " + target + "(");
        if (argumentCount != 0) {
            System.out.print(target + 1);
            for (int i = 1; i < argumentCount; i++) {
                System.out.print(", " + (target + i + 1));
            }
        }
        System.out.println(") -> " + target);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        Object[] arguments = new Object[argumentCount];
        for (int i = 0; i < argumentCount; i++) {
            arguments[i] = executionContext.get(target + i + 1);
        }
        executionContext.put(target, executionContext.invoke(executionContext.get(target), arguments));
    }
}
