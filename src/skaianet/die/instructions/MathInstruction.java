package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

import java.io.PrintStream;

/**
 * Created on 2014-07-25.
 */
public class MathInstruction implements Instruction {
    private final int target;
    private final int paramRef;
    private final Operation mathOp;

    public MathInstruction(int target, int paramRef, Operation mathOp) {
        this.target = target;
        this.paramRef = paramRef;
        this.mathOp = mathOp;
    }

    @Override
    public void print(int indent, PrintStream out) {
        out.println("MATH " + paramRef + " " + mathOp + " -> " + target);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, mathOp.apply(executionContext.get(target), executionContext.get(paramRef), executionContext));
    }

    public static enum Operation {
        ADD {
            @Override
            protected int applyInteger(int left, int right) {
                return left + right;
            }
        }, SUBTRACT {
            @Override
            protected int applyInteger(int left, int right) {
                return left - right;
            }
        }, MULTIPLY {
            @Override
            protected int applyInteger(int left, int right) {
                return left * right;
            }
        }, DIVIDE {
            @Override
            protected int applyInteger(int left, int right) {
                return left / right;
            }
        }, REMAINDER {
            @Override
            protected int applyInteger(int left, int right) {
                return left % right;
            }
        }, RASHIFT {
            @Override
            protected int applyInteger(int left, int right) {
                return left >> right;
            }
        }, RLSHIFT {
            @Override
            protected int applyInteger(int left, int right) {
                return left >>> right;
            }
        }, LSHIFT {
            @Override
            protected int applyInteger(int left, int right) {
                return left << right;
            }
        }, BIOR {
            @Override
            protected int applyInteger(int left, int right) {
                return left | right;
            }
        }, BIXOR {
            @Override
            protected int applyInteger(int left, int right) {
                return left ^ right;
            }
        }, BIAND {
            @Override
            protected int applyInteger(int left, int right) {
                return left & right;
            }
        }, LOR {
            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                return context.is(left) || context.is(right);
            }
        }, LAND {
            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                return context.is(left) && context.is(right);
            }
        }, CMPNE {
            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                return !left.equals(right);
            }
        }, CMPEQ {
            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                return left.equals(right);
            }
        }, CMPGE {
            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                return ((Comparable<Object>) left).compareTo(right) >= 0;
            }
        }, CMPGT {
            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                return ((Comparable<Object>) left).compareTo(right) > 0;
            }
        }, CMPLT {
            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                return ((Comparable<Object>) left).compareTo(right) < 0;
            }
        }, CMPLE {
            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                return ((Comparable<Object>) left).compareTo(right) <= 0;
            }
        };

        public Object apply(Object left, Object right, ExecutionContext context) {
            if (left instanceof Number && right instanceof Number) {
                return applyNumber((Number) left, (Number) right);
            } else {
                return applyObject(left, right);
            }
        }

        Object applyObject(Object left, Object right) {
            throw new IllegalArgumentException("Unsupported: " + this + " on " + left.getClass() + ", " + right.getClass());
        }

        Number applyNumber(Number left, Number right) {
            if (left instanceof Integer && right instanceof Integer) {
                return applyInteger((Integer) left, (Integer) right);
            } else {
                throw new IllegalArgumentException("Unsupported: " + this + " on " + left.getClass() + ", " + right.getClass());
            }
        }

        int applyInteger(int left, int right) {
            throw new IllegalArgumentException("Unsupported: " + this + " on integers");
        }
    }
}
