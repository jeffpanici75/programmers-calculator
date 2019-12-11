package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.Token;

public interface AstNode {
    Token getToken();

    AstNodeType getType();
}
