package skaianet.die.middle;

import skaianet.die.ast.Expression;
import skaianet.die.ast.GenericNode;
import skaianet.die.ast.Statement;
import skaianet.die.ast.StatementType;
import skaianet.die.instructions.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Compiler {
    private final ArrayList<Instruction> output = new ArrayList<>();
    private HashMap<Integer, String> debugging;
    private int nextFreeVar, maxVars;

    public CompiledProcedure compile(Statement procedure, String[] arguments) throws CompilingException {
        if (!output.isEmpty()) {
            throw new CompilingException("Malformed state of Compiler!");
        }
        debugging = new HashMap<>();
        Scope outermost = new Scope(0);
        maxVars = nextFreeVar = 1; // First variable is always the root energy context.
        for (String argument : arguments) {
            outermost.defineVar(argument, nextFreeVar++);
        }
        Integer returned;
        if (procedure.type == StatementType.RETURN || procedure.type == StatementType.COMPOUND_RETURN) {
            returned = compileReturnStatement(procedure, outermost);
        } else {
            compileStatement(procedure, outermost);
            returned = null;
        }

        Instruction[] instructions = output.toArray(new Instruction[output.size()]);
        output.clear();
        return new CompiledProcedure(arguments.length, maxVars, instructions, debugging, returned);
    }

    private void compileStatement(Statement statement, Scope scope) throws CompilingException {
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
                        compileStatement((Statement) node, inner);
                    }
                    if (nextFreeVar < prevDepth) {
                        throw new CompilingException("Internal error: lost parent scope variables!");
                    }
                    nextFreeVar = prevDepth;
                    break;
                }
                case EXPRESSION: {
                    compileExpression((Expression) statement.get(), scope);
                    freeVar();
                    break;
                }
                case ATHLOOP: {
                    statement.checkSize(3);
                    int condition = nextFreeVar;
                    compileExpression((Expression) statement.get(0), scope);
                    EnterLoopInstruction enterInstruction = new EnterLoopInstruction(condition);
                    output.add(enterInstruction);
                    int loopTop = label();
                    compileStatement((Statement) statement.get(1), scope);
                    output.add(new ExitLoopInstruction(condition, scope.getEnergyRef(), loopTop));
                    compileStatement((Statement) statement.get(2), new Scope(scope, condition));
                    enterInstruction.bind(label());
                    freeVar(condition);
                    break;
                }
                case IMPORT: {
                    statement.checkSize(2);
                    int out = nextVar();
                    output.add(new ImportInstruction(out, (String) statement.get(0).getAssoc(), (String) statement.get(1).getAssoc()));
                    scope.defineVar((String) statement.get(1).getAssoc(), out);
                    break;
                }
                case ASSIGN: {
                    statement.checkSize(2);
                    int temp = nextFreeVar;
                    compileExpression((Expression) statement.get(1), scope);
                    if (scope.isDefined((String) statement.get(0).getAssoc())) {
                        setVar(temp, scope.get((String) statement.get(0).getAssoc()));
                        freeVar(temp);
                    } else {
                        scope.defineVar((String) statement.get(0).getAssoc(), temp);
                    }
                    break;
                }
                case UTILDEF: {
                    statement.checkSize(3);
                    String name = (String) statement.get(0).getAssoc();
                    Compiler compiler = new Compiler();
                    Expression children = (Expression) statement.get(1);
                    String[] arguments = new String[children.size()];
                    for (int i = 0; i < arguments.length; i++) {
                        arguments[i] = (String) children.get(i).getAssoc();
                    }
                    CompiledProcedure procedure = compiler.compile((Statement) statement.get(2), arguments);
                    int out = nextVar();
                    output.add(new ConstantInstruction(out, procedure));
                    scope.defineVar(name, out);
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
    private int compileReturnStatement(Statement statement, Scope scope) throws CompilingException {
        debugging.put(label(), statement.getTraceInfo());
        try {
            switch (statement.type) {
                case RETURN: {
                    statement.checkSize(1);
                    int out = nextFreeVar;
                    compileExpression((Expression) statement.get(0), scope);
                    return out;
                }
                case COMPOUND_RETURN: {
                    Scope inner = new Scope(scope);
                    for (int i = 0; i < statement.size() - 1; i++) {
                        compileStatement((Statement) statement.get(i), inner);
                    }
                    return compileReturnStatement((Statement) statement.get(statement.size() - 1), inner);
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

    private void compileExpression(Expression expression, Scope scope) throws CompilingException {
        debugging.put(label(), expression.getTraceInfo());
        int varOut = nextVar();
        try {
            switch (expression.type) {
                case VARIABLE:
                    setVar(scope.get((String) expression.getAssoc()), varOut);
                    return;
                case CONST_INTEGER:
                    output.add(new ConstantInstruction(varOut, (Integer) expression.getAssoc()));
                    return;
                case CONST_STRING:
                    output.add(new ConstantInstruction(varOut, (String) expression.getAssoc()));
                    return;
                case INVOKE:
                    --nextFreeVar;
                    for (GenericNode<?, ?> exp : expression) {
                        compileExpression((Expression) exp, scope);
                    }
                    output.add(new InvokeInstruction(varOut, nextFreeVar - varOut - 1, scope.getEnergyRef()));
                    nextFreeVar = varOut + 1;
                    return;
                case FIELDREF:
                    expression.checkSize(2);
                    --nextFreeVar;
                    compileExpression((Expression) expression.get(0), scope);
                    output.add(new FieldFetchInstruction(varOut, (String) expression.get(1).getAssoc()));
                    return;
                case ARRAYREF:
                    expression.checkSize(2);
                    --nextFreeVar;
                    compileExpression((Expression) expression.get(0), scope);
                    int index = nextFreeVar;
                    compileExpression((Expression) expression.get(1), scope);
                    --nextFreeVar;
                    output.add(new ArrayFetchInstruction(varOut, index));
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
                case CMPGT:
                    expression.checkSize(2);
                    --nextFreeVar;
                    compileExpression((Expression) expression.get(0), scope);
                    int param = nextFreeVar;
                    compileExpression((Expression) expression.get(1), scope);
                    --nextFreeVar;
                    output.add(new MathInstruction(varOut, param, expression.type.getMathOp()));
                    return;
                case NULL:
                    output.add(new ConstantInstruction(varOut, ConstantInstruction.Type.NULL, null));
                    return;
                case TRUE:
                    output.add(new ConstantInstruction(varOut, true));
                    return;
                case FALSE:
                    output.add(new ConstantInstruction(varOut, false));
                    return;
                case THIS:
                    output.add(new ThisRefInstruction(varOut));
                    return;
                default:
                    throw new CompilingException("Unsupported expression: " + expression);
            }
        } catch (CompilingException ex) {
            ex.addTrace(expression.getTraceInfo());
            throw ex;
        }
    }

    private void setVar(int in, int out) {
        output.add(new MoveInstruction(in, out));
    }
}
