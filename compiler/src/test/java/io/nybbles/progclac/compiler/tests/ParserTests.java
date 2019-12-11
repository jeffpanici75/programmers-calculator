package io.nybbles.progclac.compiler.tests;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progclac.compiler.lexer.Lexer;
import io.nybbles.progclac.compiler.lexer.Token;
import io.nybbles.progclac.compiler.parser.Parser;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ParserTests {
    private Lexer _lexer;
    private ArrayList<Token> _tokens;

    @Before
    public void testSetup() {
        _lexer = new Lexer();
        _tokens = new ArrayList<>();
    }

    @Test
    public void decimalExpression() {
        var r = new Result();
        assertTrue(_lexer.tokenize(
                r,
                "5 * 2 + 1",
                _tokens));

        var parser = new Parser(_lexer.getBuffer(), _tokens);
        assertTrue(parser.initialize(r));
        var program = parser.parse(r);
        assertNotNull(program);
    }

    @Test
    public void hexExpression() {
        var r = new Result();
        assertTrue(_lexer.tokenize(
                r,
                "$0a * $1f + $2b_aa_1f",
                _tokens));

        var parser = new Parser(_lexer.getBuffer(), _tokens);
        assertTrue(parser.initialize(r));
        var program = parser.parse(r);
        assertNotNull(program);
    }

    @Test
    public void complexExpression() {
        var r = new Result();
        assertTrue(_lexer.tokenize(
                r,
                "(PI * 6.2165) / -2 + 100",
                _tokens));

        var parser = new Parser(_lexer.getBuffer(), _tokens);
        assertTrue(parser.initialize(r));
        var program = parser.parse(r);
        assertNotNull(program);
    }

}
