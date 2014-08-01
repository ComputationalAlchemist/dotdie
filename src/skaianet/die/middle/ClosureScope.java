package skaianet.die.middle;

import skaianet.die.front.ColoredIdentifier;

import java.util.ArrayList;

public class ClosureScope {
    private final Scope scope;
    private final ClosureScope parent;
    private final ArrayList<Integer> mappingToOuter = new ArrayList<>();

    public ClosureScope(Scope scope, ClosureScope parent) {
        this.scope = scope;
        this.parent = parent;
    }

    public int get(ColoredIdentifier var) throws CompilingException {
        int vi;
        if (!scope.isDefined(var) && parent != null) {
            int pid = parent.get(var);
            vi = -1 - pid;
        } else {
            vi = scope.get(var);
        }
        int out = mappingToOuter.indexOf(vi);
        if (out == -1) {
            out = mappingToOuter.size();
            mappingToOuter.add(vi);
        }
        return out;
    }

    public int[] getMapping() {
        int[] out = new int[mappingToOuter.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = mappingToOuter.get(i);
        }
        return out;
    }

    public boolean hasAny() {
        return !mappingToOuter.isEmpty();
    }

    public boolean provides(ColoredIdentifier identifier) {
        return scope.isDefined(identifier) || (parent != null && parent.provides(identifier));
    }
}
