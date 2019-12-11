package io.nybbles.progclac.compiler.tests;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progcalc.vm.Register;
import io.nybbles.progcalc.vm.VirtualMachine;
import io.nybbles.progclac.compiler.ByteCodeCompiler;
import io.nybbles.progclac.compiler.SymbolTable;
import io.nybbles.progclac.compiler.ast.ProgramAstNode;
import io.nybbles.progclac.compiler.lexer.Lexer;
import io.nybbles.progclac.compiler.lexer.Token;
import io.nybbles.progclac.compiler.parser.Parser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ByteCodeCompilerTests {
    private Result _r;
    private Lexer _lexer;
    private VirtualMachine _vm;
    private ArrayList<Token> _tokens;
    private SymbolTable _symbolTable;

    @Before
    public void testSetup() {
        _lexer = new Lexer();
        _tokens = new ArrayList<>();

        _r = new Result();
        _vm = new VirtualMachine(65535, 4096, 4096);
        assertTrue(_vm.initialize(_r));
        _symbolTable = new SymbolTable(_vm);
    }

    @After
    public void testTeardown() {
        if (!_r.isSuccess()) {
            for (var msg : _r.getMessages()) {
                System.out.println(String.format("[%s] %s", msg.getCode(), msg.getMessage()));
            }
            fail("runtime error");
        }
    }

    private Register executeExpression(String source) {
        _tokens.clear();

        _r = new Result();
        assertTrue(_lexer.tokenize(_r, source, _tokens));

        var parser = new Parser(source, _tokens);
        assertTrue(parser.initialize(_r));
        var program = parser.parse(_r);
        assertNotNull(program);

        var compiler = new ByteCodeCompiler(_vm, _symbolTable, (ProgramAstNode) program);
        var compiledProgram = compiler.compile(_r);
        assertNotNull(compiledProgram);

        System.out.println(compiledProgram.disassemble());
        _vm.setProgram(compiledProgram);
        assertTrue(_vm.run(_r));

        var targetRegister = _vm.getRegister(compiledProgram.getTargetRegister());
        if (targetRegister == null) {
            fail("Invalid target register type");
        }
        assertNotNull(targetRegister);
        return targetRegister;
    }

    @Test
    public void expressionProduces11() {
        var targetRegister = executeExpression("5 * 2 + 1");
        assertEquals(11, targetRegister.getValue().asInteger());
    }

    @Test
    public void expressionProduces145() {
        var targetRegister = executeExpression("((24 / 2) * 4) / 2 + 121");
        assertEquals(145, targetRegister.getValue().asInteger());
    }

    @Test
    public void expressionProduces9_42() {
        var targetRegister = executeExpression("3.14 * 3");
        assertEquals(9.42, targetRegister.getValue().asFloat(), 0.0);
    }

    @Test
    public void relationalOperators() {
        var targetRegister = executeExpression("1 < 2");
        assertEquals(1, targetRegister.getValue().asInteger());

        targetRegister = executeExpression("1 > 2");
        assertEquals(0, targetRegister.getValue().asInteger());

        targetRegister = executeExpression("1 <= 1");
        assertEquals(1, targetRegister.getValue().asInteger());

        targetRegister = executeExpression("3 >= 3");
        assertEquals(1, targetRegister.getValue().asInteger());
    }

    @Test
    public void logicalOperators() {
        var targetRegister = executeExpression("1 == 1 && 2 == 2");
        assertEquals(1, targetRegister.getValue().asInteger());
    }

    @Test
    public void symbolsInExpressions() {
        var targetRegister = executeExpression("PI := 3.14");
        assertEquals(3.14, targetRegister.getValue().asFloat(), 0.0);

        var pi = _symbolTable.getSymbol("PI");
        assertEquals(3.14, pi.asFloat(), 0.0);

        targetRegister = executeExpression("PI * 3 > 9; weeks := 52; salary := 3800; weeks * salary");
        assertEquals(197600, targetRegister.getValue().asInteger());
    }

}
