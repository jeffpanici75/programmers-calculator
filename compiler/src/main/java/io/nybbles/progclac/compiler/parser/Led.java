package io.nybbles.progclac.compiler.parser;

import io.nybbles.progclac.compiler.ast.AstNode;

public interface Led {
    AstNode parse(Context context, AstNode lhs);
}
