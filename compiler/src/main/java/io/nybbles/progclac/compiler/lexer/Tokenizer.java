package io.nybbles.progclac.compiler.lexer;

import io.nybbles.progcalc.common.Result;

import java.util.ArrayList;

public interface Tokenizer {
    boolean tokenize(Result r, Lexer lexer, ArrayList<Token> tokens);
}
