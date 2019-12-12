package io.nybbles.progclac.compiler.parser;

import io.nybbles.progclac.compiler.ast.StatementAstNode;

public interface Std {
    StatementAstNode parse(Context context);
}
