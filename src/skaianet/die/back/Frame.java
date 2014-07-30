package skaianet.die.back;

import skaianet.die.front.Color;
import skaianet.die.instructions.Instruction;
import skaianet.die.instructions.InvokeInstruction;
import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;
import java.util.Arrays;

class Frame {
    public final CompiledProcedure procedure;
    public final Color thread;
    final Object[] variables;
    private int codePointer = 0;

    Frame(Color thread, CompiledProcedure procedure, Object[] variables) {
        if (thread == null) {
            throw new NullPointerException();
        }
        this.thread = thread;
        this.procedure = procedure;
        this.variables = variables;
    }

    public Object getReturnValue() {
        return procedure.toReturn == null ? null : variables[procedure.toReturn];
    }

    public boolean isDone() {
        return codePointer >= procedure.instructions.length;
    }

    public int getCodePointer() {
        return codePointer;
    }

    public void execute(ExecutionContext executionContext) {
        if (codePointer == -1) { // We forked up. (Into two different threads, so we need to pretend that we're returning now.)
            codePointer = procedure.instructions.length;
            return;
        }
        int ptr = codePointer++;
        procedure.instructions[ptr].execute(thread, executionContext);
    }

    public void jump(int label) {
        codePointer = label;
    }

    public Object get(int i) {
        return variables[i];
    }

    public void put(int i, Object o) {
        variables[i] = o;
    }

    public void report(PrintStream out) {
        out.println("VARS " + Arrays.toString(variables));
        out.print(" " + procedure + "+" + codePointer + ": ");
        if (codePointer < 0 || codePointer >= procedure.instructions.length) {
            out.println("????");
        } else {
            Instruction instruction = procedure.instructions[codePointer];
            if (instruction.thread == null || instruction.thread.equals(thread)) {
                instruction.printInternal(0, out);
            } else {
                out.println("(no exec)");
            }
        }
    }

    public void returnValue(ExecutionContext exc, Object value) {
        ((InvokeInstruction) procedure.instructions[codePointer++]).onReturn(exc, value);
    }

    public void repeatInstruction() {
        codePointer--;
    }

    public void pauseForThreadRejoin() {
        codePointer = -1;
    }

    public int offset() {
        return codePointer;
    }
}
