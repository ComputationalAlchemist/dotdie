package skaianet.die.ast;

import skaianet.die.front.Color;

import java.util.Collection;

public class Statement extends GenericNode<StatementType, Statement> {

    private final Color thread;

    public Statement(String traceInfo, Color thread, StatementType type, GenericNode<?, ?>... children) {
        super(traceInfo, type, children);
        this.thread = thread;
    }

    public Statement(String traceInfo, Color thread, StatementType type, Collection<? extends GenericNode<?, ?>> children) {
        super(traceInfo, type, children);
        this.thread = thread;
    }

    public Color getThread() {
        return thread;
    }

    public Color getThread(Color alternate) {
        return alternate != null && !alternate.equals(Color.NO_THREAD) ? alternate : thread;
    }
}
