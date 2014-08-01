package skaianet.die.back;

import skaianet.die.front.Color;
import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class ExecutionWrapper {
    private static final boolean requireSingleContext = false;
    private final LinkedList<ExecutionContext> contexts = new LinkedList<>();
    private final LinkedList<ExecutionContext> queued = new LinkedList<>();
    private final HashSet<ExecutionContext> parents = new HashSet<>();

    public ExecutionWrapper(ExecutionExtension extension, CompiledProcedure main, EnergyPacket packet) {
        ExecutionContext context = new ExecutionContext(extension, this, null);
        contexts.add(context);
        context.init(main, null, packet, new Object[0]);
    }

    public void add(ExecutionContext context) {
        queued.add(context);
    }

    private boolean continueExecution(boolean step) {
        parents.clear();
        contexts.addAll(queued);
        queued.clear();
        for (ExecutionContext ec : contexts) {
            if (ec.parent != null) {
                parents.add(ec.parent);
            }
        }
        Iterator<ExecutionContext> iterator = contexts.iterator();
        while (iterator.hasNext()) {
            ExecutionContext ec = iterator.next();
            if (!parents.contains(ec)) {
                if (!(step ? ec.runStep() : ec.runSweep())) {
                    if (!ec.isTerminatedNormally()) {
                        throw new RuntimeException("Thread failed to terminate normally!");
                    }
                    iterator.remove();
                }
            }
        }
        contexts.addAll(queued);
        queued.clear();
        return !contexts.isEmpty();
    }

    public boolean continueExecution() {
        return continueExecution(false);
    }

    public boolean stepExecution() {
        return continueExecution(true);
    }

    public Object getThisObject(ExecutionContext parent, Color thisColor) {
        for (ExecutionContext ec : contexts) {
            if (ec.parent == parent && ec.isRootColor(thisColor)) {
                return ec.getATHThis(thisColor);
            }
        }
        throw new RuntimeException("No such thread: " + parent + "/" + thisColor);
    }

    public void report(PrintStream out) {
        if (requireSingleContext) {
            if (contexts.size() != 1) {
                throw new RuntimeException("Set requireSingleContext to false!");
            }
        } else {
            out.println("Contexts: " + contexts.size());
        }
        int i = 0;
        for (ExecutionContext context : contexts) {
            if (!requireSingleContext) {
                out.print(i++ + " ");
            }
            context.report(out);
        }
    }
}
