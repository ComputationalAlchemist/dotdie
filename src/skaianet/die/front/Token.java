package skaianet.die.front;

import skaianet.die.ast.ExpressionType;

public enum Token {
    NONE, OPEN_CURLY, CLOSE_CURLY, SEMICOLON, INTEGER, DOUBLE, STRING, IDENTIFIER, OPEN_PAREN, CLOSE_PAREN,
    EXECUTE, DOT, OPEN_SQUARE, CLOSE_SQUARE, ATH, COMMA, IMPORT, THIS, UTILDEF, RETURN, BIFURCATE,
    // Arithmetic/logical infix operators!
    MULTIPLY(ExpressionType.MULTIPLY), DIVIDE(ExpressionType.DIVIDE), REMAINDER(ExpressionType.REMAINDER),
    SUBTRACT(ExpressionType.SUBTRACT), ADD(ExpressionType.ADD),
    BIAND(ExpressionType.BIAND), BIXOR(ExpressionType.BIXOR), BIOR(ExpressionType.BIOR),
    LSHIFT(ExpressionType.LSHIFT), RLSHIFT(ExpressionType.RLSHIFT), RASHIFT(ExpressionType.RASHIFT),
    LAND(ExpressionType.LAND), LOR(ExpressionType.LOR), NOT(ExpressionType.NOT),
    // Comparison infix operators!
    CMPLT(ExpressionType.CMPLT), CMPGT(ExpressionType.CMPGT),
    CMPLE(ExpressionType.CMPLE), CMPGE(ExpressionType.CMPGE),
    CMPNE(ExpressionType.CMPNE), CMPEQ(ExpressionType.CMPEQ),
    // Assignment operators!
    SET(),
    SETADD(ExpressionType.ADD), SETSUBTRACT(ExpressionType.SUBTRACT),
    SETMULTIPLY(ExpressionType.MULTIPLY), SETDIVIDE(ExpressionType.DIVIDE), SETREMAINDER(ExpressionType.REMAINDER),
    SETLSHIFT(ExpressionType.LSHIFT), SETRLSHIFT(ExpressionType.RLSHIFT), SETRASHIFT(ExpressionType.RASHIFT),
    SETAND(ExpressionType.BIAND), SETOR(ExpressionType.BIOR), SETXOR(ExpressionType.BIXOR), NULL, TRUE, FALSE;
    private final ExpressionType expressionType;

    private Token() {
        expressionType = null;
    }

    private Token(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public static Token lookupIdentifierToken(String identifier) {
        switch (identifier.toUpperCase()) {
            case "~ATH":
                return ATH;
            case "EXECUTE":
                return EXECUTE;
            case "IMPORT":
                return IMPORT;
            case "NULL":
                return NULL;
            case "FALSE":
                return FALSE;
            case "TRUE":
                return TRUE;
            case "THIS":
                return THIS;
            case "U~F":
                return UTILDEF;
            case "RETURN":
                return RETURN;
            case "BIFURCATE":
                return BIFURCATE;
            default:
                return null;
        }
    }

    public ExpressionType getExpressionType() {
        return expressionType;
    }
}
