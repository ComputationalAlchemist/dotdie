package skaianet.die.ast;

import java.util.Collection;

/**
 * Created on 2014-07-23.
 */
public enum StatementType {
    EMPTY, COMPOUND, EXPRESSION, ATHLOOP, IMPORT, ASSIGN;

    public Statement make(String traceInfo, GenericNode<?, ?>... children) {
        return new Statement(traceInfo, this, children);
    }

    public Statement make(String traceInfo, Collection<? extends GenericNode<?, ?>> children) {
        return new Statement(traceInfo, this, children);
    }
}
