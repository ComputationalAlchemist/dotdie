package skaianet.die.middle;

import skaianet.die.instructions.Instruction;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created on 2014-07-24.
 */
public class CompiledProcedure {
    public final int argumentCount;
    public final int maxVars;
    public final Instruction[] instructions;
    public final HashMap<Integer, String> debugging;
    public final Integer toReturn;

    public CompiledProcedure(int argumentCount, int maxVars, Instruction[] instructions, HashMap<Integer, String> debugging, Integer toReturn) {
        this.argumentCount = argumentCount;
        this.maxVars = maxVars;
        this.instructions = instructions;
        this.debugging = debugging;
        this.toReturn = toReturn;
    }

    private static void indent(int indent) {
        char[] out = new char[indent];
        Arrays.fill(out, '\t');
        System.out.print(out);
    }

    public void print(int indent) {
        indent(indent);
        System.out.println("Procedure[" + argumentCount + "/" + maxVars + "]: " + instructions.length);
        int i = 0;
        String prev = null;
        for (Instruction instruction : instructions) {
            String s = debugging.get(i);
            if (s != null && !s.equals(prev)) {
                indent(indent);
                System.out.println(s);
                prev = s;
            }
            indent(indent);
            System.out.print((i++) + "\t");
            indent(indent);
            instruction.print(indent);
        }
    }
}
