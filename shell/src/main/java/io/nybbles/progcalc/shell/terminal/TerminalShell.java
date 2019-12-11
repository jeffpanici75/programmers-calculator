package io.nybbles.progcalc.shell.terminal;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import io.nybbles.progcalc.common.Result;
import io.nybbles.progcalc.common.ResultMessageType;
import io.nybbles.progcalc.common.StopWatch;
import io.nybbles.progcalc.math.NumberHelpers;
import io.nybbles.progcalc.shell.CommandHistory;
import io.nybbles.progcalc.shell.Constants;
import io.nybbles.progcalc.shell.contracts.Configuration;
import io.nybbles.progcalc.shell.contracts.Shell;
import io.nybbles.progcalc.vm.NumericType;
import io.nybbles.progcalc.vm.Program;
import io.nybbles.progcalc.vm.Register;
import io.nybbles.progcalc.vm.VirtualMachine;
import io.nybbles.progclac.compiler.ByteCodeCompiler;
import io.nybbles.progclac.compiler.SymbolTable;
import io.nybbles.progclac.compiler.ast.ProgramAstNode;
import io.nybbles.progclac.compiler.lexer.Lexer;
import io.nybbles.progclac.compiler.lexer.Token;
import io.nybbles.progclac.compiler.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Scanner;

public class TerminalShell implements Shell {
    private static final Logger s_logger = LoggerFactory.getLogger(TerminalShell.class);
    private CommandHistory _history = new CommandHistory();
    private Scanner _scanner = new Scanner(System.in);
    private Configuration _configuration;
    private Lexer _lexer = new Lexer();
    private SymbolTable _symbolTable;
    private String _historyLine = "";
    private ColoredPrinter _printer;
    private VirtualMachine _vm;
    private boolean _running;

    private void formatResult(Result result) {
        for (var msg : result.getMessages()) {
            var color = msg.getType() == ResultMessageType.Error ?
                    Ansi.FColor.RED :
                    Ansi.FColor.YELLOW;
            var formatted = String.format(
                    "[%s] %s\n",
                    msg.getCode(),
                    msg.getMessage());
            format(Ansi.Attribute.BOLD, color, Ansi.BColor.BLACK, formatted);

            var detail = msg.getDetails();
            if (detail != null && detail.length() > 0) {
                format(
                        Ansi.Attribute.BOLD,
                        color,
                        Ansi.BColor.BLACK,
                        "       %s\n", detail);
            }
        }
    }

    private void homeCursor() {
        _printer.print("\033[H");
    }

    private void clearScreen() {
        _printer.print("\033[2J");
    }

    private void formatPrompt() {
        var buffer = new StringBuilder();
        if (_vm.isStackEmpty())
            buffer.append("(empty):0");
        else {
            var tos = _vm.topOfStack();
            buffer.append("(");
            buffer.append(NumberHelpers.hexStringForSize(tos, 64));
            buffer.append(":");
            buffer.append(_vm.getStackDepth());
            buffer.append(")");
        }
        format("%s > %s", buffer.toString(), _historyLine);
        if (_historyLine.length() > 0)
            _historyLine = "";
    }

    private void formatDisassembly(Program program) {
        var lines = program.disassemble();
        for (var line : lines)
            System.out.println(String.format(" %s", line));
    }

    private Register executeExpression(
            Result r,
            String source) {
        var tokens = new ArrayList<Token>();

        if (!_lexer.tokenize(r, source, tokens))
            return null;

        var parser = new Parser(source, tokens);
        if (!parser.initialize(r))
            return null;

        var program = (ProgramAstNode) parser.parse(r);
        if (program == null)
            return null;

        program.setSource(source);
        var compiler = new ByteCodeCompiler(_vm, _symbolTable, program);
        var compiledProgram = compiler.compile(r);
        if (compiledProgram == null)
            return null;

        formatDisassembly(compiledProgram);
        _vm.setProgram(compiledProgram);
        if (!_vm.run(r))
            return null;

        return _vm.getRegister(compiledProgram.getTargetRegister());
    }

    public TerminalShell(Configuration configuration) {
        _configuration = configuration;
        _printer = new ColoredPrinter.Builder(1, false)
                .foreground(Ansi.FColor.WHITE)
                .background(Ansi.BColor.BLACK)
                .build();
    }

    @Override
    public Result run() {
        var r = new Result();
        var stopwatch = new StopWatch();
        _running = true;

        while (_running) {
            formatPrompt();

            var line = _scanner.nextLine();
            if (line.length() == 0)
                continue;

            stopwatch.start();
            var executeResult = new Result();
            try {
                var targetRegister = executeExpression(executeResult, line);
                if (targetRegister == null)
                    formatResult(executeResult);
                else {
                    var type = targetRegister.getType().getNumericType();
                    var typeName = switch (type) {
                        case Integer -> "U64";
                        case Float   -> "F64";
                    };
                    var value = switch (type) {
                        case Integer -> targetRegister.getValue().asInteger();
                        case Float   -> Double.doubleToRawLongBits(targetRegister.getValue().asFloat());
                    };

                    format(
                            Ansi.Attribute.REVERSE,
                            Ansi.FColor.BLUE,
                            Ansi.BColor.YELLOW,
                            " Type: %s                                                          ",
                            typeName);
                    format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n");

                    format(
                            Ansi.Attribute.REVERSE,
                            Ansi.FColor.BLUE,
                            Ansi.BColor.YELLOW,
                            " QDWORD                                                             ");
                    format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n");

                    switch (targetRegister.getType()) {
                        case U0, U64 -> format(
                                Ansi.Attribute.NONE,
                                Ansi.FColor.GREEN,
                                Ansi.BColor.NONE,
                                String.format(" %d\n", value));
                        case F64 -> format(
                                Ansi.Attribute.NONE,
                                Ansi.FColor.GREEN,
                                Ansi.BColor.NONE,
                                String.format(" %f\n", targetRegister.getValue().asFloat()));
                    }

                    format(
                            Ansi.Attribute.NONE,
                            Ansi.FColor.GREEN,
                            Ansi.BColor.NONE,
                            " %%%s\n",
                            NumberHelpers.binaryStringForSize(value, 64));

                    format(
                            Ansi.Attribute.NONE,
                            Ansi.FColor.GREEN,
                            Ansi.BColor.NONE,
                            " $%s\n",
                            NumberHelpers.hexStringForSize(value, 64));

                    format(
                            Ansi.Attribute.NONE,
                            Ansi.FColor.GREEN,
                            Ansi.BColor.NONE,
                            " @%s\n",
                            NumberHelpers.octalStringForSize(value, 64));
                }
            } finally {
                var elapsedTime = stopwatch.stop();
                String timeValue = "";
                var milliSeconds = elapsedTime.toMillis();
                if (milliSeconds > 0) {
                    timeValue = String.format("%dms", milliSeconds);
                } else {
                    var nanoSeconds = elapsedTime.toNanos();
                    timeValue = String.format("%dus", nanoSeconds / 1000L);
                }
                format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n");
                format(
                        Ansi.Attribute.NONE,
                        Ansi.FColor.YELLOW,
                        Ansi.BColor.NONE,
                        "execution time: %s",
                        timeValue);
                format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n\n");
            }
        }

        _printer.clear();

        return r;
    }

    @Override
    public void format(
            Ansi.Attribute attribute,
            Ansi.FColor foregroundColor,
            Ansi.BColor backgroundColor,
            String fmt,
            Object... args) {
        _printer.print(
                String.format(fmt, args),
                attribute,
                foregroundColor,
                backgroundColor);
        _printer.clear();
    }

    @Override
    public boolean initialize(Result r) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            format(
                    Ansi.Attribute.NONE,
                    Ansi.FColor.BLUE,
                    Ansi.BColor.CYAN,
                    "Bye!");
            _running = false;
        }));

        _vm = new VirtualMachine(65535, 4096, 4096);
        if (!_vm.initialize(r))
            return false;
        _symbolTable = new SymbolTable(_vm);

        _vm.addTrapHandler(
                Constants.Traps.QUIT,
                (result, vm) -> {
                    _running = false;
                    return true;
                });
        _vm.addTrapHandler(
                Constants.Traps.CLEAR_SCREEN,
                (result, vm) -> {
                    clearScreen();
                    homeCursor();
                    return true;
                });
        _vm.addTrapHandler(
                io.nybbles.progcalc.vm.Constants.Traps.PRINT,
                (result, vm) -> {
                    var numericTypes = NumericType.values();
                    var count = vm.pop();
                    for (var i = 0; i < count; i++) {
                        var type = numericTypes[(int) vm.pop()];
                        var typeName = switch (type) {
                            case Integer -> "U64";
                            case Float   -> "F64";
                        };
                        var value = switch (type) {
                            case Integer -> vm.pop();
                            case Float   -> Double.doubleToRawLongBits(vm.popDouble());
                        };
                        format(
                                Ansi.Attribute.REVERSE,
                                Ansi.FColor.BLUE,
                                Ansi.BColor.YELLOW,
                                " Type: %s                                                      ",
                                typeName);
                        format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n");
                        format(
                                Ansi.Attribute.REVERSE,
                                Ansi.FColor.BLUE,
                                Ansi.BColor.YELLOW,
                                " BYTE      WORD              DWORD                              ");
                        format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n");
                        format(
                                Ansi.Attribute.NONE,
                                Ansi.FColor.GREEN,
                                Ansi.BColor.NONE,
                                " %%%s %%%s %%%s\n",
                                NumberHelpers.binaryStringForSize(value, 8),
                                NumberHelpers.binaryStringForSize(value, 16),
                                NumberHelpers.binaryStringForSize(value, 32));

                        format(
                                Ansi.Attribute.NONE,
                                Ansi.FColor.GREEN,
                                Ansi.BColor.NONE,
                                " $%s $%s $%s\n",
                                NumberHelpers.hexStringForSize(value, 8),
                                NumberHelpers.hexStringForSize(value, 16),
                                NumberHelpers.hexStringForSize(value, 32));

                        format(
                                Ansi.Attribute.NONE,
                                Ansi.FColor.GREEN,
                                Ansi.BColor.NONE,
                                " @%s @%s @%s\n",
                                NumberHelpers.octalStringForSize(value, 8),
                                NumberHelpers.octalStringForSize(value, 16),
                                NumberHelpers.octalStringForSize(value, 32));
                    }

                    format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n");

                    return true;
                });

        format(
                Ansi.Attribute.REVERSE,
                Ansi.FColor.BLUE,
                Ansi.BColor.CYAN,
                "      Programmers' Calculator, version %s          ",
                Constants.PROGRAM_VERSION);
        format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n");
        format(
                Ansi.Attribute.NONE,
                Ansi.FColor.BLUE,
                Ansi.BColor.BLACK,
                "%s",
                Constants.COPYRIGHT_NOTICE);
        format(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE, "\n\n");

        return true;
    }

    @Override
    public void format(String fmt, Object... args) {
        _printer.print(String.format(fmt, args));
    }
}
