package skaianet.die.back;

import skaianet.die.middle.CompiledProcedure;

public class Closure {
    public final CompiledProcedure procedure;
    public final Object[] upvalues;

    public Closure(CompiledProcedure procedure, Object[] upvalues) {
        this.procedure = procedure;
        this.upvalues = upvalues;
    }
}
