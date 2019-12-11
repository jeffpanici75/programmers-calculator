package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class UnaryOperatorAstNode implements AstNode {
    private UnaryOperatorType _type;
    private AstNode _lhs;
    private Token _token;

    public UnaryOperatorAstNode(
            UnaryOperatorType type,
            AstNode lhs,
            Token token) {
        _lhs = lhs;
        _type = type;
        _token = token;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.UnaryOperator;
    }

    public AstNode getLhs() {
        return _lhs;
    }

    public UnaryOperatorType getOperatorType() {
        return _type;
    }
}
