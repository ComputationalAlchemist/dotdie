package skaianet.die.middle;

import skaianet.die.instructions.Instruction;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

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

    private static void indent(int indent, PrintStream out) {
        char[] chars = new char[indent];
        Arrays.fill(chars, '\t');
        out.print(chars);
    }

    public void print(int indent, PrintStream out) {
        indent(indent, out);
        out.println("Procedure[" + argumentCount + "/" + maxVars + "]: " + instructions.length);
        int i = 0;
        String prev = null;
        for (Instruction instruction : instructions) {
            String s = debugging.get(i);
            if (s != null && !s.equals(prev)) {
                indent(indent, out);
                out.println(s);
                prev = s;
            }
            indent(indent, out);
            out.print((i++) + "\t");
            indent(indent, out);
            instruction.print(indent, out);
        }
    }
}
