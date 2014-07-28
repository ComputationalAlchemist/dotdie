package skaianet.die.ast;

import java.util.Collection;

public class Expression extends GenericNode<ExpressionType, Expression> {
    public Expression(String traceInfo, ExpressionType type, GenericNode<?, ?>... children) {
        super(traceInfo, type, children);
    }

    public Expression(String traceInfo, ExpressionType type, Collection<? extends GenericNode<?, ?>> children) {
        super(traceInfo, type, children);
    }

    public Expression(String traceInfo, ExpressionType type, Object assoc) {
        super(traceInfo, type, assoc);
    }
}
