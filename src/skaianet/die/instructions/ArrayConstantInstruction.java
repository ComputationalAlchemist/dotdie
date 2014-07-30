package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class ArrayConstantInstruction extends Instruction {
    private final int target;
    private final int count;

    public ArrayConstantInstruction(Color thread, int target, int count) {
        super(thread);
        this.target = target;
        this.count = count;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        if (count == 0) {
            out.println("ARRAY (empty) -> " + target);
        } else {
            out.println("ARRAY " + target + "..." + (count + target - 1) + " -> " + target);
        }
    }

    @Override
    protected void execute(ExecutionContext context) {
        Object[] out = new Object[count];
        for (int i = 0; i < count; i++) {
            out[i] = context.get(target + i);
        }
        context.put(target, out);
    }
}
