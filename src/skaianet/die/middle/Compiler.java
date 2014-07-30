package skaianet.die.middle;

import skaianet.die.ast.*;
import skaianet.die.front.Color;
import skaianet.die.front.ColoredIdentifier;
import skaianet.die.instructions.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Compiler {
    private final ArrayList<Instruction> output = new ArrayList<>();
    private HashMap<Integer, String> debugging;
    private int nextFreeVar, maxVars;

    public CompiledProcedure compile(Statement procedure, ColoredIdentifier[] arguments) throws CompilingException {
        if (!output.isEmpty()) {
            throw new CompilingException("Malformed state of Compiler!");
        }
        debugging = new HashMap<>();
        Scope outermost = new Scope(0);
        maxVars = nextFreeVar = 1; // First variable is always the root energy context.
        for (ColoredIdentifier argument : arguments) {
            outermost.defineVar(argument, nextFreeVar++);
        }
        Integer returned;
        if (procedure.type == StatementType.RETURN || procedure.type == StatementType.COMPOUND_RETURN) {
            returned = compileReturnStatement(procedure, outermost, null);
        } else {
            compileStatement(procedure, outermost, null);
            returned = null;
        }

        Instruction[] instructions = output.toArray(new Instruction[output.size()]);
        output.clear();
        return new CompiledProcedure(arguments.length, maxVars, instructions, debugging, returned);
    }

    private void compileStatement(Statement statement, Scope scope, Color executionThread) throws CompilingException {
        debugging.put(label(), statement.getTraceInfo());
        try {
            switch (statement.type) {
                case EMPTY: {
                    statement.checkSize(0);
                    // No code to generate.
                    break;
                }
                case COMPOUND: {
                    int prevDepth = nextFreeVar;
                    Scope inner = new Scope(scope);
                    for (GenericNode<?, ?> node : statement) {
                        compileStatement((Statement) node, inner, executionThread);
                    }
                    if (nextFreeVar < prevDepth) {
                        throw new CompilingException("Internal error: lost parent scope variables!");
                    }
                    nextFreeVar = prevDepth;
                    break;
                }
                case EXPRESSION: {
                    compileExpression((Expression) statement.get(), scope, statement.getThread(executionThread));
                    freeVar();
                    break;
                }
                case ATHLOOP: {
                    executionThread = statement.getThread(executionThread);
                    statement.checkSize(3);
                    int condition = nextFreeVar;
                    compileExpression((Expression) statement.get(0), scope, executionThread);
                    EnterLoopInstruction enterInstruction = new EnterLoopInstruction(executionThread, condition);
                    output.add(enterInstruction);
                    int loopTop = label();
                    compileStatement((Statement) statement.get(1), scope, executionThread);
                    output.add(new ExitLoopInstruction(executionThread, condition, scope.getEnergyRef(), loopTop));
                    compileStatement((Statement) statement.get(2), new Scope(scope, condition), executionThread);
                    enterInstruction.bind(label());
                    freeVar(condition);
                    break;
                }
                case IMPORT: {
                    statement.checkSize(2);
                    int out = nextVar();
                    output.add(new ImportInstruction(statement.getThread(executionThread), out, (ColoredIdentifier) statement.get(0).getAssoc(), (ColoredIdentifier) statement.get(1).getAssoc()));
                    scope.defineVar((ColoredIdentifier) statement.get(1).getAssoc(), out);
                    break;
                }
                case ASSIGN: {
                    statement.checkSize(2);
                    int temp = nextFreeVar;
                    compileExpression((Expression) statement.get(1), scope, statement.getThread(executionThread));
                    if (scope.isDefined((ColoredIdentifier) statement.get(0).getAssoc())) {
                        output.add(new MoveInstruction(statement.getThread(executionThread), temp, scope.get((ColoredIdentifier) statement.get(0).getAssoc())));
                        freeVar(temp);
                    } else {
                        scope.defineVar((ColoredIdentifier) statement.get(0).getAssoc(), temp);
                    }
                    break;
                }
                case UTILDEF: {
                    statement.checkSize(3);
                    ColoredIdentifier name = (ColoredIdentifier) statement.get(0).getAssoc();
                    Compiler compiler = new Compiler();
                    Expression children = (Expression) statement.get(1);
                    ColoredIdentifier[] arguments = new ColoredIdentifier[children.size()];
                    for (int i = 0; i < arguments.length; i++) {
                        arguments[i] = (ColoredIdentifier) children.get(i).getAssoc();
                    }
                    CompiledProcedure procedure = compiler.compile((Statement) statement.get(2), arguments);
                    int out = nextVar();
                    output.add(new ConstantInstruction(statement.getThread(executionThread), out, procedure));
                    scope.defineVar(name, out);
                    break;
                }
                case BIFURCATE_THREAD: {
                    statement.checkSize(2);
                    Expression a = (Expression) statement.get(0), b = (Expression) statement.get(1);
                    if (a.type != ExpressionType.THIS || b.type != ExpressionType.THIS) {
                        throw new CompilingException("Unexpected non-THIS in BIFURCATE_THREAD children.");
                    }
                    Color colorA = (Color) a.getAssoc(), colorB = (Color) b.getAssoc();
                    output.add(new ThreadBifurcationInstruction(statement.getThread(executionThread), colorA, colorB));
                    break;
                }
                default:
                    throw new CompilingException("Unhandled statement: " + statement.type);
            }
        } catch (CompilingException ex) {
            ex.addTrace(statement.getTraceInfo());
            throw ex;
        }
    }

    // Callers must throw away any extra variables!
    private int compileReturnStatement(Statement statement, Scope scope, Color executionThread) throws CompilingException {
        debugging.put(label(), statement.getTraceInfo());
        try {
            switch (statement.type) {
                case RETURN: {
                    statement.checkSize(1);
                    int out = nextFreeVar;
                    compileExpression((Expression) statement.get(0), scope, statement.getThread(executionThread));
                    return out;
                }
                case COMPOUND_RETURN: {
                    executionThread = statement.getThread(executionThread);
                    Scope inner = new Scope(scope);
                    for (int i = 0; i < statement.size() - 1; i++) {
                        compileStatement((Statement) statement.get(i), inner, executionThread);
                    }
                    return compileReturnStatement((Statement) statement.get(statement.size() - 1), inner, executionThread);
                }
                default:
                    throw new CompilingException("Unhandled return statement: " + statement.type);
            }
        } catch (CompilingException ex) {
            ex.addTrace(statement.getTraceInfo());
            throw ex;
        }
    }

    private int label() {
        return output.size();
    }

    private void freeVar(int var) throws CompilingException {
        if (var != --nextFreeVar) {
            throw new CompilingException("Malformed variable tracking: " + var + " instead of " + nextFreeVar);
        }
    }

    private void freeVar() {
        --nextFreeVar;
    }

    private int nextVar() {
        int out = nextFreeVar++;
        maxVars = Math.max(maxVars, nextFreeVar);
        return out;
    }

    private void compileExpression(Expression expression, Scope scope, Color executionThread) throws CompilingException {
        debugging.put(label(), expression.getTraceInfo());
        int varOut = nextVar();
        try {
            switch (expression.type) {
                case VARIABLE:
                    output.add(new MoveInstruction(executionThread, scope.get((ColoredIdentifier) expression.getAssoc()), varOut));
                    return;
                case CONST_INTEGER:
                    output.add(new ConstantInstruction(executionThread, varOut, (Integer) expression.getAssoc()));
                    return;
                case CONST_STRING:
                    output.add(new ConstantInstruction(executionThread, varOut, (String) expression.getAssoc()));
                    return;
                case INVOKE:
                    --nextFreeVar;
                    for (GenericNode<?, ?> exp : expression) {
                        compileExpression((Expression) exp, scope, executionThread);
                    }
                    output.add(new InvokeInstruction(executionThread, varOut, nextFreeVar - varOut - 1, scope.getEnergyRef()));
                    nextFreeVar = varOut + 1;
                    return;
                case FIELDREF:
                    expression.checkSize(2);
                    --nextFreeVar;
                    compileExpression((Expression) expression.get(0), scope, executionThread);
                    output.add(new FieldFetchInstruction(executionThread, varOut, (ColoredIdentifier) expression.get(1).getAssoc()));
                    return;
                case ARRAYREF:
                    expression.checkSize(2);
                    --nextFreeVar;
                    compileExpression((Expression) expression.get(0), scope, executionThread);
                    int index = nextFreeVar;
                    compileExpression((Expression) expression.get(1), scope, executionThread);
                    --nextFreeVar;
                    output.add(new ArrayFetchInstruction(executionThread, varOut, index));
                    return;
                case ARRAYCONST:
                    --nextFreeVar;
                    for (GenericNode<?, ?> node : expression) {
                        compileExpression((Expression) node, scope, executionThread);
                    }
                    output.add(new ArrayConstantInstruction(executionThread, varOut, expression.size()));
                    nextFreeVar = varOut + 1;
                    return;
                case ADD:
                case SUBTRACT:
                case MULTIPLY:
                case DIVIDE:
                case REMAINDER:
                case BIAND:
                case BIXOR:
                case BIOR:
                case RASHIFT:
                case RLSHIFT:
                case LSHIFT:
                case LOR:
                case LAND:
                case CMPLT:
                case CMPLE:
                case CMPNE:
                case CMPEQ:
                case CMPGE:
                case CMPGT: // Binary operators
                    expression.checkSize(2);
                    --nextFreeVar;
                    compileExpression((Expression) expression.get(0), scope, executionThread);
                    int param = nextFreeVar;
                    compileExpression((Expression) expression.get(1), scope, executionThread);
                    --nextFreeVar;
                    output.add(new MathInstruction(executionThread, varOut, param, expression.type.getMathOp()));
                    return;
                case NOT: // Unary operators
                    expression.checkSize(1);
                    --nextFreeVar;
                    compileExpression((Expression) expression.get(0), scope, executionThread);
                    output.add(new MathInstruction(executionThread, varOut, -1, expression.type.getMathOp()));
                    return;
                case NULL:
                    output.add(new ConstantInstruction(executionThread, varOut, ConstantInstruction.Type.NULL, null));
                    return;
                case TRUE:
                    output.add(new ConstantInstruction(executionThread, varOut, true));
                    return;
                case FALSE:
                    output.add(new ConstantInstruction(executionThread, varOut, false));
                    return;
                case THIS:
                    output.add(new ThisRefInstruction(executionThread, varOut, (Color) expression.getAssoc()));
                    return;
                default:
                    throw new CompilingException("Unsupported expression: " + expression.type);
            }
        } catch (CompilingException ex) {
            ex.addTrace(expression.getTraceInfo());
            throw ex;
        }
    }
}
