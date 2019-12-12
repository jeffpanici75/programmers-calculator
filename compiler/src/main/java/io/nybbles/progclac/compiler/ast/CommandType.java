package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.TokenType;

public enum CommandType {
    Print;

    public static CommandType fromTokenType(TokenType tokenType) {
        return switch (tokenType) {
            case Question -> CommandType.Print;
            default -> null;
        };
    }
}
