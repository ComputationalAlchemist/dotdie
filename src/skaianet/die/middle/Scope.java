package skaianet.die.middle;

import java.util.HashMap;

/**
 * Created on 2014-07-24.
 */
public class Scope {
    private final Scope parent;
    private final int energyRef;
    private final HashMap<String, Integer> variables = new HashMap<>();

    public Scope(int energyRef) {
        this.parent = null;
        this.energyRef = energyRef;
    }

    public Scope(Scope parent) {
        this.parent = parent;
        this.energyRef = parent.energyRef;
    }

    public Scope(Scope parent, int energyRef) {
        this.parent = parent;
        this.energyRef = energyRef;
    }

    public void defineVar(String var, int id) throws CompilingException {
        if (variables.containsKey(var)) {
            throw new CompilingException("Variable already defined: " + var);
        }
        variables.put(var, id);
    }

    public int get(String var) throws CompilingException {
        if (variables.containsKey(var)) {
            return variables.get(var);
        } else if (parent != null) {
            return parent.get(var);
        } else {
            throw new CompilingException("Variable not defined: " + var);
        }
    }

    public boolean isDefined(String var) {
        return variables.containsKey(var) || (parent != null && parent.isDefined(var));
    }

    public int getEnergyRef() {
        return energyRef;
    }
}
