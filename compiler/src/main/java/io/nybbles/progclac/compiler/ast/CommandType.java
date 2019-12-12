package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.TokenType;

public enum CommandType {
    Pop,
    Push,
    Clear,
    Print;

    public static CommandType fromTokenType(TokenType tokenType) {
        return switch (tokenType) {
            case Pop      -> CommandType.Pop;
            case Push     -> CommandType.Push;
            case Clear    -> CommandType.Clear;
            case Question -> CommandType.Print;
            default -> null;
        };
    }
}
