package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

import java.io.PrintStream;

public class ImportInstruction implements Instruction {
    private final int target;
    private final String namespace;
    private final String name;

    public ImportInstruction(int target, String namespace, String name) {
        this.target = target;
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public void print(int indent, PrintStream out) {
        out.println("IMPORT " + namespace + "/" + name + " -> " + target);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.calcImport(namespace, name));
    }
}
