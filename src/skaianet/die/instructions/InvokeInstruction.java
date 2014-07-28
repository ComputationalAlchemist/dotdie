package skaianet.die.instructions;

import skaianet.die.back.EnergyPacket;
import skaianet.die.back.ExecutionContext;
import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;

public class InvokeInstruction implements Instruction {
    private final int target;
    private final int argumentCount;
    private final int energyRef;

    public InvokeInstruction(int target, int argumentCount, int energyRef) {
        this.target = target;
        this.argumentCount = argumentCount;
        this.energyRef = energyRef;
    }

    @Override
    public void print(int indent, PrintStream out) {
        out.print("INVOKE " + target + "(");
        if (argumentCount != 0) {
            out.print(target + 1);
            for (int i = 1; i < argumentCount; i++) {
                out.print(", " + (target + i + 1));
            }
        }
        out.println(") -> " + target);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        Object[] arguments = new Object[argumentCount];
        for (int i = 0; i < argumentCount; i++) {
            arguments[i] = executionContext.get(target + i + 1);
        }
        Object procedure = executionContext.get(target);
        if (procedure instanceof CompiledProcedure) {
            executionContext.repeatInstruction();
            executionContext.init((CompiledProcedure) procedure, (EnergyPacket) executionContext.get(energyRef), arguments);
        } else {
            executionContext.put(target, executionContext.invoke(procedure, arguments));
        }
    }

    public void onReturn(ExecutionContext executionContext, Object value) {
        executionContext.put(target, value);
    }
}
