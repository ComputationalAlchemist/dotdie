package skaianet.die.ast;

import java.util.Collection;

/**
 * Created on 2014-07-23.
 */
public class Statement extends GenericNode<StatementType, Statement> {

    public Statement(String traceInfo, StatementType type, GenericNode<?, ?>... children) {
        super(traceInfo, type, children);
    }

    public Statement(String traceInfo, StatementType type, Collection<? extends GenericNode<?, ?>> children) {
        super(traceInfo, type, children);
    }
}
