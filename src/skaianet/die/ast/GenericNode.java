package skaianet.die.ast;

import skaianet.die.middle.CompilingException;

import java.io.PrintStream;
import java.util.*;

/**
 * Created on 2014-07-23.
 */
public class GenericNode<T extends Enum, U extends GenericNode<T, U>> implements Iterable<GenericNode<?, ?>> {
    public final T type;
    private final List<GenericNode<?, ?>> children;
    private final Object assoc;
    private final String traceInfo;

    GenericNode(String traceInfo, T type, GenericNode<?, ?>... children) {
        if (type == null) {
            throw new NullPointerException();
        }
        this.traceInfo = traceInfo;
        this.type = type;
        this.children = Collections.unmodifiableList(Arrays.asList(children));
        this.assoc = null;
    }

    GenericNode(String traceInfo, T type, Collection<? extends GenericNode<?, ?>> children) {
        if (type == null) {
            throw new NullPointerException();
        }
        this.traceInfo = traceInfo;
        this.type = type;
        this.children = Collections.unmodifiableList(new ArrayList<>(children));
        this.assoc = null;
    }

    GenericNode(String traceInfo, T type, Object associated) {
        if (type == null || associated == null) {
            throw new NullPointerException();
        }
        this.traceInfo = traceInfo;
        this.type = type;
        this.children = Arrays.asList();
        this.assoc = associated;
    }

    public GenericNode<?, ?> get() throws CompilingException {
        if (children.size() != 1) {
            throw new CompilingException("No-argument get() only works when there is exactly one child element!");
        }
        return children.get(0);
    }

    public void print() {
        print(System.out);
    }

    public void print(PrintStream out) {
        print(out, 0);
    }

    void print(PrintStream out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print('\t');
        }
        if (assoc != null) {
            out.println(type + ": " + assoc);
        } else {
            out.println(type);
        }
        for (GenericNode<?, ?> child : children) {
            child.print(out, indent + 1);
        }
    }

    @Override
    public Iterator<GenericNode<?, ?>> iterator() {
        return children.iterator();
    }

    public void checkSize(int len) throws CompilingException {
        if (children.size() != len) {
            throw new CompilingException("Internal error: bad size of " + this);
        }
    }

    public Object getAssoc() throws CompilingException {
        if (assoc == null) {
            throw new CompilingException("Internal error: no associated value!");
        }
        checkSize(0);
        return assoc;
    }

    public GenericNode<?, ?> get(int i) throws CompilingException {
        if (i >= children.size()) {
            throw new CompilingException("Internal error: not enough children!");
        }
        return children.get(i);
    }

    public String getTraceInfo() {
        return traceInfo;
    }

    public int size() {
        return children.size();
    }
}
