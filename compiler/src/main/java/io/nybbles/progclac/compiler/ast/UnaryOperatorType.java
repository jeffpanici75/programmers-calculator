package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.TokenType;

public enum UnaryOperatorType {
    Negate,
    BinaryNot;

    public static UnaryOperatorType fromTokenType(TokenType tokenType) {
        return switch (tokenType) {
            case Minus -> UnaryOperatorType.Negate;
            case Tilde -> UnaryOperatorType.BinaryNot;
            default -> null;
        };
    }
}
