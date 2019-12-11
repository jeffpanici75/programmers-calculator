package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class AssignmentAstNode implements AstNode {
    private AstNode _identifier;
    private AstNode _value;
    private Token _token;

    public AssignmentAstNode(AstNode identifier, AstNode value, Token token) {
        _value = value;
        _token = token;
        _identifier = identifier;
    }

    @Override
    public Token getToken() {
        return _token;
    }

    public AstNode getValue() {
        return _value;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.Assignment;
    }

    public AstNode getIdentifier() {
        return _identifier;
    }
}
