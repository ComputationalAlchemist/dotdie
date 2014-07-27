package skaianet.die.instructions;

import skaianet.die.back.EnergyPacket;
import skaianet.die.back.ExecutionContext;
import skaianet.die.back.LoopContext;

/**
 * Created on 2014-07-25.
 */
public class ExitLoopInstruction implements Instruction {
    private final int context, parentEnergy;
    private final Integer loopTop;

    public ExitLoopInstruction(int context, int parentEnergy, int loopTop) {
        this.context = context;
        this.parentEnergy = parentEnergy;
        this.loopTop = loopTop;
    }

    @Override
    public void print() {
        System.out.println("EXIT " + context + " -> " + loopTop);
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
