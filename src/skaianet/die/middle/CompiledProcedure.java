package skaianet.die.middle;

import skaianet.die.instructions.Instruction;

import java.util.HashMap;

/**
 * Created on 2014-07-24.
 */
public class CompiledProcedure {
    public final int argcount;
    public final int maxVars;
    public final Instruction[] instructions;
    public final HashMap<Integer, String> debugging;

    public CompiledProcedure(int argcount, int maxVars, Instruction[] instructions, HashMap<Integer, String> debugging) {
        this.argcount = argcount;
        this.maxVars = maxVars;
        this.instructions = instructions;
        this.debugging = debugging;
    }

    public void print() {
        System.out.println("Procedure[" + argcount + "/" + maxVars + "]: " + instructions.length);
        int i = 0;
        String prev = null;
        for (Instruction instruction : instructions) {
            String s = debugging.get(i);
            if (s != null && !s.equals(prev)) {
                System.out.println(s);
                prev = s;
            }
            System.out.print((i++) + "\t");
            instruction.print();
        }
    }
}
