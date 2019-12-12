package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class CommandAstNode implements AstNode {
    private AstNode _expression;
    private CommandType _type;
    private Token _token;

    public CommandAstNode(CommandType type, AstNode expression, Token token) {
        _type = type;
        _token = token;
        _expression = expression;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.Command;
    }

    public AstNode getExpression() {
        return _expression;
    }

    public CommandType getCommandType() {
        return _type;
    }
}
