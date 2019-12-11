package io.nybbles.progclac.compiler.parser;

import io.nybbles.progclac.compiler.ast.AstNode;

public interface Nud {
    AstNode parse(Context context);
}
