package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

import java.util.ArrayList;

public class ProgramAstNode implements AstNode {
    private ArrayList<StatementAstNode> _statements;
    private String _source;

    public ProgramAstNode(ArrayList<StatementAstNode> statements) {
        _statements = statements;
    }

    @Override
    public Token getToken() {
        return null;
    }

    public String getSource() {
        return _source;
    }

    @Override
    public AstNodeType getType() {
        return AstNodeType.Program;
    }

    public void setSource(String source) {
        _source = source;
    }

    public ArrayList<StatementAstNode> getStatements() {
        return _statements;
    }
}
