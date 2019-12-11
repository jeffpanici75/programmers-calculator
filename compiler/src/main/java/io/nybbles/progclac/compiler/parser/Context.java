package io.nybbles.progclac.compiler.parser;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progclac.compiler.lexer.Token;

public class Context {
    public Result r;
    public Token token;
    public Parser parser;
    public ProductionRule rule;
}
