package io.nybbles.progclac.compiler.tests;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progclac.compiler.lexer.Lexer;
import io.nybbles.progclac.compiler.lexer.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class LexerTests {
    private Lexer _lexer;
    private ArrayList<Token> _tokens;

    @Before
    public void testSetup() {
        _lexer = new Lexer();
        _tokens = new ArrayList<>();
    }

//    @After
//    public void testTearDown() {
//        for (var token : _tokens) {
//            System.out.println(token.getSlice());
//        }
//    }

    @Test
    public void simpleDecimalExpression() {
        var r = new Result();
        var success = _lexer.tokenize(
                r,
                "5 * 2 + 1",
                _tokens);
        assertTrue(success);
    }

    @Test
    public void simpleBinaryExpression() {
        var r = new Result();
        var success = _lexer.tokenize(
                r,
                "%0000_1111 * %1101_0011 + %0000_0001",
                _tokens);
        assertTrue(success);
    }

    @Test
    public void simpleOctalExpression() {
        var r = new Result();
        var success = _lexer.tokenize(
                r,
                "@777 * @033 + @212",
                _tokens);
        assertTrue(success);
    }

    @Test
    public void simpleHexExpression() {
        var r = new Result();
        var success = _lexer.tokenize(
                r,
                "$05 * $ff + $1A",
                _tokens);
        assertTrue(success);
    }

    @Test
    public void characterLiteralExpressions() {
        var r = new Result();
        var success = _lexer.tokenize(
                r,
                "'A' + 'c'",
                _tokens);
        assertTrue(success);
    }

    @Test
    public void identifierAndLiteralCombinedExpressions() {
        var r = new Result();
        var success = _lexer.tokenize(
                r,
                "(PI * 6.2165) / 2 + 100",
                _tokens);
        assertTrue(success);
    }

}
