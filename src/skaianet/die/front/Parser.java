package skaianet.die.front;

import skaianet.die.ast.Expression;
import skaianet.die.ast.GenericNode;
import skaianet.die.ast.Statement;

import java.io.PrintStream;
import java.util.ArrayList;

import static skaianet.die.ast.ExpressionType.*;
import static skaianet.die.ast.StatementType.*;

public class Parser {

    private static final int UNARY_INDEX = 1;
    private static final Token[][] operators = new Token[][]{
            {Token.OPEN_PAREN, Token.OPEN_SQUARE, Token.DOT},
            {Token.NOT}, // This index must be in UNARY_INDEX above.
            {Token.MULTIPLY, Token.DIVIDE, Token.REMAINDER},
            {Token.ADD, Token.SUBTRACT},
            {Token.BIAND},
            {Token.BIXOR},
            {Token.BIOR},
            {Token.LSHIFT, Token.RLSHIFT, Token.RASHIFT},
            {Token.CMPLT, Token.CMPGT, Token.CMPLE, Token.CMPGE},
            {Token.CMPEQ, Token.CMPNE},
            {Token.LAND},
            {Token.LOR}
    };
    private final Tokenizer tokenizer;
    private Token active;
    private Object assoc;
    private Color color;

    public Parser(String filename, String code) {
        tokenizer = new Tokenizer(filename, code);
    }

    public Statement parseProgram() throws ParsingException {
        Statement result = parseBlock(true);
        expect(Token.NONE); // End of file.
        return result;
    }

    private Statement parseStatement(boolean nullable, boolean canBeReturn, boolean needsSemicolon) throws ParsingException {
        String traceInfo = tokenizer.traceInfo();
        pull();
        Color thread = color;
        switch (accept(Token.OPEN_CURLY, Token.SEMICOLON, Token.ATH, Token.IMPORT, Token.UTILDEF, Token.RETURN, Token.BIFURCATE)) {
            case OPEN_CURLY: {
                Statement out = parseBlock(canBeReturn);
                expect(Token.CLOSE_CURLY);
                return out;
            }
            case SEMICOLON:
                return EMPTY.make(traceInfo, thread);
            case ATH: {
                expect(Token.OPEN_PAREN);
                Expression condition = parseExpression(false);
                expect(Token.CLOSE_PAREN);
                Statement stmt = parseStatement(false, false, true);
                expect(Token.EXECUTE);
                expect(Token.OPEN_PAREN);
                Statement exec = parseStatement(false, false, false);
                expect(Token.CLOSE_PAREN);
                if (needsSemicolon) {
                    expect(Token.SEMICOLON);
                } else {
                    accept(Token.SEMICOLON);
                }
                return ATHLOOP.make(traceInfo, thread, condition, stmt, exec);
            }
            case IMPORT: {
                expect(Token.IDENTIFIER);
                Expression identifier = VARIABLE.make(traceInfo, new ColoredIdentifier((String) assoc, color));
                expect(Token.IDENTIFIER);
                Expression spec = VARIABLE.make(traceInfo, new ColoredIdentifier((String) assoc, color));
                if (needsSemicolon) {
                    expect(Token.SEMICOLON);
                } else {
                    accept(Token.SEMICOLON);
                }
                return IMPORT.make(traceInfo, thread, identifier, spec);
            }
            case UTILDEF: {
                expect(Token.IDENTIFIER);
                Expression name = VARIABLE.make(traceInfo, new ColoredIdentifier((String) assoc, color));
                expect(Token.OPEN_PAREN);
                Expression[] args;
                if (accept(Token.IDENTIFIER)) {
                    ArrayList<Expression> arguments = new ArrayList<>();
                    arguments.add(VARIABLE.make(traceInfo, new ColoredIdentifier((String) assoc, color)));
                    while (accept(Token.COMMA)) {
                        expect(Token.IDENTIFIER);
                        arguments.add(VARIABLE.make(traceInfo, new ColoredIdentifier((String) assoc, color)));
                    }
                    args = arguments.toArray(new Expression[arguments.size()]);
                } else {
                    args = new Expression[0];
                }
                expect(Token.CLOSE_PAREN);
                Statement stmt = parseStatement(false, true, true);
                return UTILDEF.make(traceInfo, thread, name, ARGLIST.make(traceInfo, args), stmt);
            }
            case RETURN: {
                if (!canBeReturn) {
                    throw new ParsingException("Found RETURN where disallowed!");
                }
                Expression expression = parseExpression(false);
                if (needsSemicolon) {
                    expect(Token.SEMICOLON);
                } else {
                    accept(Token.SEMICOLON);
                }
                return RETURN.make(traceInfo, thread, expression);
            }
            case BIFURCATE: {
                expect(Token.THIS); // TODO: Allow for variable bifurcation.
                Color orig = color;
                if (!orig.equals(thread)) {
                    throw new ParsingException("Cannot bifurcate a different thread!");
                }
                expect(Token.OPEN_SQUARE);
                expect(Token.THIS);
                Color a = color;
                expect(Token.COMMA);
                expect(Token.THIS);
                Color b = color;
                expect(Token.CLOSE_SQUARE);
                expect(Token.SEMICOLON);
                return BIFURCATE_THREAD.make(traceInfo, thread, THIS.make(traceInfo, a), THIS.make(traceInfo, b));
            }
            default: {
                Expression expression = parseExpression(nullable);
                if (expression == null) {
                    return null;
                }
                if (isAssignableValue(expression)) {
                    pull();
                    Color lthread = color; // Use color from the assignment operator.
                    Token t = accept(Token.SET, Token.SETADD, Token.SETSUBTRACT, Token.SETMULTIPLY, Token.SETDIVIDE, Token.SETREMAINDER,
                            Token.SETLSHIFT, Token.SETRLSHIFT, Token.SETRASHIFT, Token.SETAND, Token.SETOR, Token.SETXOR);
                    if (t != Token.NONE) {
                        Expression param = parseExpression(false);
                        if (needsSemicolon) {
                            expect(Token.SEMICOLON);
                        } else {
                            accept(Token.SEMICOLON);
                        }
                        if (t != Token.SET) {
                            param = t.getExpressionType().make(traceInfo, expression, param);
                        }
                        return ASSIGN.make(traceInfo, lthread, expression, param);
                    }
                }
                if (needsSemicolon) {
                    expect(Token.SEMICOLON);
                } else {
                    accept(Token.SEMICOLON);
                }
                return EXPRESSION.make(traceInfo, thread, expression);
            }
        }
    }

    private boolean isAssignableValue(Expression expression) {
        return expression.type == VARIABLE || expression.type == FIELDREF;
    }

    private Expression parseExpression(boolean nullable) throws ParsingException {
        return parseExpression(operators.length, nullable);
    }

    private Expression parseExpression(int level, boolean nullable) throws ParsingException {
        String traceInfo = tokenizer.traceInfo();
        if (level < 0) {
            throw new IllegalArgumentException("Bad precedence level: " + level);
        } else if (level == 0) {
            switch (accept(Token.INTEGER, Token.DOUBLE, Token.STRING, Token.IDENTIFIER, Token.OPEN_PAREN, Token.OPEN_SQUARE, Token.NULL, Token.TRUE, Token.FALSE, Token.THIS)) {
                case INTEGER:
                    return CONST_INTEGER.make(traceInfo, assoc);
                case DOUBLE:
                    return CONST_DOUBLE.make(traceInfo, assoc);
                case STRING:
                    return CONST_STRING.make(traceInfo, assoc);
                case IDENTIFIER:
                    return VARIABLE.make(traceInfo, new ColoredIdentifier((String) assoc, color));
                case OPEN_PAREN:
                    Expression out = parseExpression(false);
                    expect(Token.CLOSE_PAREN);
                    return out;
                case OPEN_SQUARE:
                    ArrayList<Expression> listing = new ArrayList<>();
                    Expression initial = parseExpression(true);
                    if (initial != null) {
                        listing.add(initial);
                        while (accept(Token.COMMA)) {
                            listing.add(parseExpression(false));
                        }
                    }
                    expect(Token.CLOSE_SQUARE);
                    return ARRAYCONST.make(traceInfo, listing);
                case NULL:
                    return NULL.make(traceInfo);
                case TRUE:
                    return TRUE.make(traceInfo);
                case FALSE:
                    return FALSE.make(traceInfo);
                case THIS:
                    return THIS.make(traceInfo, color);
                default:
                    if (nullable) {
                        return null;
                    } else {
                        throw new ParsingException("Bad expression token: " + active);
                    }
            }
        } else if (level == UNARY_INDEX + 1) {
            // Unary operation.
            Token operator;
            ArrayList<Token> unaries = new ArrayList<>();
            while ((operator = accept(operators[UNARY_INDEX])) != Token.NONE) {
                unaries.add(operator);
            }
            Expression expression = parseExpression(level - 1, nullable && unaries.isEmpty());
            for (int i = unaries.size() - 1; i >= 0; i--) {
                expression = unaries.get(i).getExpressionType().make(traceInfo, new GenericNode<?, ?>[]{expression});
            }
            return expression;
        } else {
            Expression left = parseExpression(level - 1, nullable);
            if (left == null) {
                return null;
            }
            Token operator;
            Expression total = left;
            while ((operator = accept(operators[level - 1])) != Token.NONE) {
                if (operator == Token.OPEN_PAREN) { // Function calls
                    ArrayList<Expression> out = new ArrayList<>();
                    out.add(total);
                    Expression firstArgument = parseExpression(true);
                    if (firstArgument == null) {
                        expect(Token.CLOSE_PAREN);
                    } else {
                        out.add(firstArgument);
                        while (accept(Token.COMMA)) {
                            out.add(parseExpression(false));
                        }
                        expect(Token.CLOSE_PAREN);
                    }
                    total = INVOKE.make(traceInfo, out);
                } else if (operator == Token.DOT) { // Field fetches
                    expect(Token.IDENTIFIER);
                    total = FIELDREF.make(traceInfo, total, VARIABLE.make(traceInfo, new ColoredIdentifier((String) assoc, color)));
                } else if (operator == Token.OPEN_SQUARE) { // Array fetches
                    total = ARRAYREF.make(traceInfo, total, parseExpression(false));
                    expect(Token.CLOSE_SQUARE);
                } else { // Other operators
                    total = operator.getExpressionType().make(traceInfo, total, parseExpression(level - 1, false));
                }
            }
            return total;
        }
    }

    private Statement parseBlock(boolean canBeReturn) throws ParsingException {
        String traceInfo = tokenizer.traceInfo();
        ArrayList<Statement> statements = new ArrayList<>();
        while (true) {
            Statement next = parseStatement(true, canBeReturn, true);
            if (next == null) {
                return COMPOUND.make(traceInfo, null, statements);
            }
            statements.add(next);
            if (next.type == RETURN || next.type == COMPOUND_RETURN) {
                return COMPOUND_RETURN.make(traceInfo, null, statements);
            }
        }
    }

    private void pull() throws ParsingException {
        if (active == null) {
            active = tokenizer.next();
            assoc = tokenizer.getAssociated();
            color = tokenizer.getAssocColor();
        }
    }

    private void consume() {
        active = null;
    }

    private boolean accept(Token token) throws ParsingException {
        pull();
        if (token == active) {
            consume();
            return true;
        }
        return false;
    }

    private Token accept(Token... acceptable) throws ParsingException {
        pull();
        for (Token t : acceptable) {
            if (t == active) {
                consume();
                return t;
            }
        }
        return Token.NONE;
    }

    private void expect(Token token) throws ParsingException {
        pull();
        if (token == active) {
            consume();
        } else {
            throw new ParsingException("Found token " + active + " with " + assoc + " but expected: " + token);
        }
    }

    public void dumpCode(PrintStream out) {
        tokenizer.dumpCode(out);
    }
}
