package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;
import skaianet.die.front.ColoredIdentifier;

import java.io.PrintStream;

public class ImportInstruction extends Instruction {
    private final int target;
    private final ColoredIdentifier namespace;
    private final ColoredIdentifier name;

    public ImportInstruction(Color thread, int target, ColoredIdentifier namespace, ColoredIdentifier name) {
        super(thread);
        this.target = target;
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("IMPORT " + namespace + "/" + name + " -> " + target);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.calcImport(namespace, name));
    }
}
