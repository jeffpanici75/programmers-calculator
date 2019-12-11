package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class IntegerLiteralAstNode implements AstNode {
    private int _radix;
    private Long _value;
    private Token _token;
    private boolean _signed;
    private IntegerSize _size;

    public IntegerLiteralAstNode(
            Long value,
            int radix,
            boolean signed,
            IntegerSize size,
            Token token) {
        _size = size;
        _token = token;
        _value = value;
        _radix = radix;
        _signed = signed;
    }

    public int getRadix() {
        return _radix;
    }

    public Long getValue() {
        return _value;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    public boolean isSigned() {
        return _signed;
    }

    public IntegerSize getSize() {
        return _size;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.IntegerLiteral;
    }
}
