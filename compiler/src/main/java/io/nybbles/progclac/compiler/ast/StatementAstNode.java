package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public class StatementAstNode implements AstNode {
    private AstNode _expression;

    public StatementAstNode(AstNode expression) {
        _expression = expression;
    }

    @Override
    public Token getToken() {
        return null;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.Statement;
    }

    public AstNode getExpression() {
        return _expression;
    }
}
