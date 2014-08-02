package skaianet.die.instructions;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class MathInstruction extends Instruction {
    private final int target;
    private final int paramRef;
    private final Operation mathOp;

    public MathInstruction(Color thread, int target, int paramRef, Operation mathOp) {
        super(thread);
        this.target = target;
        this.paramRef = paramRef;
        this.mathOp = mathOp;
        if (mathOp == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        if (paramRef == -1) {
            out.println("MATH UNARY " + mathOp + " -> " + target);
        } else {
            out.println("MATH " + paramRef + " " + mathOp + " -> " + target);
        }
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        if (paramRef == -1) {
            executionContext.put(target, mathOp.applyUnary(executionContext.get(target), executionContext));
        } else {
            executionContext.put(target, mathOp.apply(executionContext.get(target), executionContext.get(paramRef), executionContext));
        }
    }

    public static enum Operation {
        CONCATENATE {
            public Object applyObject(Object left, Object right) {
                return left.toString() + right.toString();
            }
        }, ADD {
            @Override
            protected int applyInteger(int left, int right) {
                return left + right;
            }

            public Object applyUnary(Object param, ExecutionContext context) {
                if (param instanceof Integer) {
                    return param;
                } else {
                    throw new IllegalArgumentException("Unsupported: unary + on " + param);
                }
            }
        }, SUBTRACT {
            @Override
            protected int applyInteger(int left, int right) {
                return left - right;
            }

            public Object applyUnary(Object param, ExecutionContext context) {
                if (param instanceof Integer) {
                    return -(Integer) param;
                } else {
                    throw new IllegalArgumentException("Unsupported: unary + on " + param);
                }
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
        }, NOT { // UNARY OPERATION

            @Override
            public Object apply(Object left, Object right, ExecutionContext context) {
                throw new IllegalArgumentException("Not a binary operator: " + this);
            }

            public Object applyUnary(Object param, ExecutionContext context) {
                if (param == null) {
                    return true;
                } else if (param instanceof Boolean) {
                    return !(Boolean) param;
                } else if (param instanceof Number) {
                    return ((Number) param).doubleValue() == 0;
                } else if (param instanceof ATHAlive) {
                    final ATHAlive outer = (ATHAlive) param;
                    return new ATHAlive() {
                        @Override
                        public boolean isAlive() {
                            return !outer.isAlive();
                        }

                        @Override
                        public double getEnergy() {
                            return 0; // No energy available through this abstraction. Should this be different?
                        }

                        @Override
                        public String toString() {
                            return "!" + outer;
                        }
                    };
                } else {
                    throw new IllegalArgumentException("Unsupported: ! on " + param);
                }
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

        public Object applyUnary(Object param, ExecutionContext context) {
            throw new IllegalArgumentException("Not an unary operator: " + this);
        }
    }
}
