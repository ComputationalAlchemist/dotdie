package skaianet.die.ast;

import java.util.Collection;

public enum StatementType {
    EMPTY, COMPOUND, EXPRESSION, ATHLOOP, IMPORT, ASSIGN, UTILDEF, RETURN, COMPOUND_RETURN;

    public Statement make(String traceInfo, GenericNode<?, ?>... children) {
        return new Statement(traceInfo, this, children);
    }

    public Statement make(String traceInfo, Collection<? extends GenericNode<?, ?>> children) {
        return new Statement(traceInfo, this, children);
    }
}
