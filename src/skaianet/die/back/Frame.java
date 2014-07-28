package skaianet.die.back;

import skaianet.die.instructions.InvokeInstruction;
import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;
import java.util.Arrays;

class Frame {
    private final CompiledProcedure procedure;
    private final Object[] variables;
    private int codePointer = 0;

    public Frame(CompiledProcedure procedure, Object[] variables) {
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
        procedure.instructions[codePointer++].execute(executionContext);
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
        out.println(procedure + "+" + codePointer + " " + Arrays.toString(variables));
    }

    public void returnValue(ExecutionContext exc, Object value) {
        ((InvokeInstruction) procedure.instructions[codePointer++]).onReturn(exc, value);
    }

    public void repeatInstruction() {
        codePointer--;
    }
}
