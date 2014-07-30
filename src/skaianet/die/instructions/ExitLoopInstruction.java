package skaianet.die.instructions;

import skaianet.die.back.EnergyPacket;
import skaianet.die.back.ExecutionContext;
import skaianet.die.back.LoopContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class ExitLoopInstruction extends Instruction {
    private final int context, parentEnergy;
    private final Integer loopTop;

    public ExitLoopInstruction(Color thread, int context, int parentEnergy, int loopTop) {
        super(thread);
        this.context = context;
        this.parentEnergy = parentEnergy;
        this.loopTop = loopTop;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("EXIT " + context + " -> " + loopTop);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        LoopContext ctx = (LoopContext) executionContext.get(context);
        if (ctx.continueLoop()) {
            executionContext.jump(loopTop);
        } else {
            executionContext.put(context, ctx.extractEnergy((EnergyPacket) executionContext.get(parentEnergy)));
        }
    }
}
