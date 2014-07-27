package skaianet.die.front;

import skaianet.die.ast.Expression;
import skaianet.die.ast.Statement;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import static skaianet.die.ast.ExpressionType.*;
import static skaianet.die.ast.StatementType.*;

/**
 * Created on 2014-07-23.
 */
public class Parser {

    private static final Token[][] operators = new Token[][]{
            {Token.OPEN_PAREN, Token.OPEN_SQUARE, Token.DOT}, // TODO: Unary expressions
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

    public Parser(String filename, String code) {
        tokenizer = new Tokenizer(filename, code);
    }

    public Parser(String filename, Reader reader) throws IOException {
        tokenizer = new Tokenizer(filename, reader);
    }

    public Statement parseProgram() throws ParsingException {
        Statement result = parseBlock();
        expect(Token.NONE); // End of file.
        return result;
    }

    public Statement parseStatement(boolean nullable) throws ParsingException {
        String traceInfo = tokenizer.traceInfo();
        switch (accept(Token.OPEN_CURLY, Token.SEMICOLON, Token.ATH, Token.IMPORT)) {
            case OPEN_CURLY: {
                Statement out = parseBlock();
                expect(Token.CLOSE_CURLY);
                return out;
            }
            case SEMICOLON:
                return EMPTY.make(traceInfo);
            case ATH:
                expect(Token.OPEN_PAREN);
                Expression condition = parseExpression(false);
                expect(Token.CLOSE_PAREN);
                Statement stmt = parseStatement(false);
                expect(Token.EXECUTE);
                Statement exec = parseStatement(false);
                return ATHLOOP.make(traceInfo, condition, stmt, exec);
            case IMPORT:
                expect(Token.IDENTIFIER);
                Expression identifier = VARIABLE.make(traceInfo, assoc);
                expect(Token.IDENTIFIER);
                Expression spec = VARIABLE.make(traceInfo, assoc);
                expect(Token.SEMICOLON);
                return IMPORT.make(traceInfo, identifier, spec);
            default: {
                Expression expression = parseExpression(nullable);
                if (expression == null) {
                    return null;
                }
                if (expression.type == VARIABLE) {
                    Token t = accept(Token.SET, Token.SETADD, Token.SETSUBTRACT, Token.SETMULTIPLY, Token.SETDIVIDE, Token.SETREMAINDER,
                            Token.SETLSHIFT, Token.SETRLSHIFT, Token.SETRASHIFT, Token.SETAND, Token.SETOR, Token.SETXOR);
                    if (t != Token.NONE) {
                        Expression param = parseExpression(false);
                        expect(Token.SEMICOLON);
                        if (t != Token.SET) {
                            param = t.getExpressionType().make(traceInfo, expression, param);
                        }
                        return ASSIGN.make(traceInfo, expression, param);
                    }
                }
                expect(Token.SEMICOLON);
                return EXPRESSION.make(traceInfo, expression);
            }
        }
    }

    public Expression parseExpression(boolean nullable) throws ParsingException {
        return parseExpression(operators.length, nullable);
    }

    private Expression parseExpression(int level, boolean nullable) throws ParsingException {
        String traceInfo = tokenizer.traceInfo();
        if (level < 0) {
            throw new IllegalArgumentException("Bad precedence level: " + level);
        } else if (level == 0) {
            switch (accept(Token.INTEGER, Token.STRING, Token.IDENTIFIER, Token.OPEN_PAREN, Token.NULL, Token.TRUE, Token.FALSE, Token.THIS)) {
                case INTEGER:
                    return CONST_INTEGER.make(traceInfo, assoc);
                case STRING:
                    return CONST_STRING.make(traceInfo, assoc);
                case IDENTIFIER:
                    return VARIABLE.make(traceInfo, assoc);
                case OPEN_PAREN:
                    Expression out = parseExpression(false);
                    expect(Token.CLOSE_PAREN);
                    return out;
                case NULL:
                    return NULL.make(traceInfo);
                case TRUE:
                    return TRUE.make(traceInfo);
                case FALSE:
                    return FALSE.make(traceInfo);
                case THIS:
                    return THIS.make(traceInfo);
                default:
                    if (nullable) {
                        return null;
                    } else {
                        throw new ParsingException("Bad expression token: " + active);
                    }
            }
        } else {
            Expression left = parseExpression(level - 1, nullable);
            if (left == null) {
                return null;
            }
            Token operator;
            Expression total = left;
            while ((operator = accept(operators[level - 1])) != Token.NONE) {
                if (operator == Token.OPEN_PAREN) { // Function calls
                    ArrayList<Expression> out = new ArrayList<Expression>();
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
                    total = FIELDREF.make(traceInfo, total, VARIABLE.make(traceInfo, assoc));
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

    public Statement parseBlock() throws ParsingException {
        String traceInfo = tokenizer.traceInfo();
        ArrayList<Statement> statements = new ArrayList<Statement>();
        while (true) {
            Statement next = parseStatement(true);
            if (next == null) {
                return COMPOUND.make(traceInfo, statements);
            }
            statements.add(next);
        }
    }

    public void pull() throws ParsingException {
        if (active == null) {
            active = tokenizer.next();
            assoc = tokenizer.getAssociated();
        }
    }

    public void consume() {
        active = null;
    }

    public boolean accept(Token token) throws ParsingException {
        pull();
        if (token == active) {
            consume();
            return true;
        }
        return false;
    }

    public Token accept(Token... acceptable) throws ParsingException {
        pull();
        for (Token t : acceptable) {
            if (t == active) {
                consume();
                return t;
            }
        }
        return Token.NONE;
    }

    public void expect(Token token) throws ParsingException {
        pull();
        if (token == active) {
            consume();
        } else {
            throw new ParsingException("Found token " + active + " but expected: " + token);
        }
    }

    public void dumpCode() {
        tokenizer.dumpCode();
    }
}
