package skaianet.die.ast;

import skaianet.die.instructions.MathInstruction.Operation;

import java.util.Collection;

public enum ExpressionType {
    VARIABLE, CONST_INTEGER, CONST_STRING, INVOKE, FIELDREF, ARRAYREF, NULL, TRUE, FALSE, THIS, ARGLIST,
    ADD(Operation.ADD), SUBTRACT(Operation.SUBTRACT), MULTIPLY(Operation.MULTIPLY), DIVIDE(Operation.DIVIDE), REMAINDER(Operation.REMAINDER),
    BIAND(Operation.BIAND), BIXOR(Operation.BIXOR), BIOR(Operation.BIOR), RASHIFT(Operation.RASHIFT), LOR(Operation.LOR),
    RLSHIFT(Operation.RLSHIFT), LSHIFT(Operation.LSHIFT), LAND(Operation.LAND), CMPLT(Operation.CMPLT), CMPLE(Operation.CMPLE),
    CMPNE(Operation.CMPNE), CMPEQ(Operation.CMPEQ), CMPGE(Operation.CMPGE), CMPGT(Operation.CMPGT), NOT(Operation.NOT);
    private final Operation mathOp;

    ExpressionType() {
        this.mathOp = null;
    }

    ExpressionType(Operation mathOp) {
        this.mathOp = mathOp;
    }

    public Expression make(String traceInfo, GenericNode<?, ?>... children) {
        return new Expression(traceInfo, this, children);
    }

    public Expression make(String traceInfo, Collection<? extends GenericNode<?, ?>> children) {
        return new Expression(traceInfo, this, children);
    }

    public Expression make(String traceInfo, Object assoc) {
        return new Expression(traceInfo, this, assoc);
    }

    public Operation getMathOp() {
        return mathOp;
    }
}
