package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class IdentifierAstNode implements AstNode {
    private String _value;
    private Token _token;

    public IdentifierAstNode(String value, Token token) {
        _value = value;
        _token = token;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    public String getValue() {
        return _value;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.Identifier;
    }
}
