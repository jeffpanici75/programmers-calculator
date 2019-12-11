package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class CharacterLiteralAstNode implements AstNode {
    private Character _value;
    private Token _token;

    public CharacterLiteralAstNode(Character value, Token token) {
        _value = value;
        _token = token;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    public Character getValue() {
        return _value;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.CharacterLiteral;
    }
}
