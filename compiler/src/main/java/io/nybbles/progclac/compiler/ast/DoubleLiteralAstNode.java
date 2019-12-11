package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class DoubleLiteralAstNode implements AstNode {
    private Token _token;
    private Double _value;
    private FloatSize _size;

    public DoubleLiteralAstNode(Double value, FloatSize size, Token token) {
        _size = size;
        _token = token;
        _value = value;
    }

    public Double getValue() {
        return _value;
    }

    public FloatSize getSize() {
        return _size;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.FloatLiteral;
    }
}
