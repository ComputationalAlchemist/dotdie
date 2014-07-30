package skaianet.die.ast;

import skaianet.die.front.Color;

import java.util.Collection;

public enum StatementType {
    EMPTY, COMPOUND, EXPRESSION, ATHLOOP, IMPORT, ASSIGN, UTILDEF, RETURN, COMPOUND_RETURN, BIFURCATE_THREAD;

    public Statement make(String traceInfo, Color thread, GenericNode<?, ?>... children) {
        return new Statement(traceInfo, thread, this, children);
    }

    public Statement make(String traceInfo, Color thread, Collection<? extends GenericNode<?, ?>> children) {
        return new Statement(traceInfo, thread, this, children);
    }
}
