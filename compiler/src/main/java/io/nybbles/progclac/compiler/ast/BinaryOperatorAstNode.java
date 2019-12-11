package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class BinaryOperatorAstNode implements AstNode {
    private AstNode _lhs;
    private AstNode _rhs;
    private Token _token;
    private BinaryOperatorType _type;

    public BinaryOperatorAstNode(
            BinaryOperatorType type,
            AstNode lhs,
            AstNode rhs,
            Token token) {
        _lhs = lhs;
        _rhs = rhs;
        _type = type;
        _token = token;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.BinaryOperator;
    }

    public AstNode getLhs() {
        return _lhs;
    }

    public AstNode getRhs() {
        return _rhs;
    }

    public BinaryOperatorType getOperatorType() {
        return _type;
    }
}
