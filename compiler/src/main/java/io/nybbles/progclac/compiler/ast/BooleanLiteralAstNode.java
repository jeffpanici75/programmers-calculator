package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class BooleanLiteralAstNode implements AstNode {
    private Boolean _value;
    private Token _token;

    public BooleanLiteralAstNode(Boolean value, Token token) {
        _value = value;
        _token = token;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    public Boolean getValue() {
        return _value;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.BooleanLiteral;
    }
}
