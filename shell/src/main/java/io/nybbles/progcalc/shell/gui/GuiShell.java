package io.nybbles.progcalc.shell.gui;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progcalc.common.ResultMessageType;
import io.nybbles.progcalc.common.StopWatch;
import io.nybbles.progcalc.math.NumberHelpers;
import io.nybbles.progcalc.shell.CommandHistory;
import io.nybbles.progcalc.shell.Constants;
import io.nybbles.progcalc.shell.contracts.Configuration;
import io.nybbles.progcalc.shell.contracts.DeferredTrap;
import io.nybbles.progcalc.shell.contracts.GraphicalShell;
import io.nybbles.progcalc.shell.contracts.TrapContext;
import io.nybbles.progcalc.vm.*;
import io.nybbles.progclac.compiler.ByteCodeCompiler;
import io.nybbles.progclac.compiler.SymbolTable;
import io.nybbles.progclac.compiler.ast.ProgramAstNode;
import io.nybbles.progclac.compiler.lexer.Lexer;
import io.nybbles.progclac.compiler.lexer.Token;
import io.nybbles.progclac.compiler.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import static io.nybbles.progcalc.shell.gui.StringHelpers.padString;

public class GuiShell extends JFrame
        implements GraphicalShell, KeyListener {
    private static final Logger s_logger = LoggerFactory.getLogger(GuiShell.class);
    private static Color[] s_gradientColors = {
            new Color(0, 0, 200),
            new Color(0, 0, 149),
            new Color(0, 0, 98)};

    private static final class TrapCapture {
        public DeferredTrap handler;
        public TrapContext context = new TrapContext();
    }

    private ArrayList<TrapCapture> _deferredTraps = new ArrayList<>();
    private CommandHistory _history = new CommandHistory();
    private JLayeredPane _layeredPane = new JLayeredPane();
    private StopWatch _stopWatch = new StopWatch();
    private Configuration _configuration;
    private boolean _insertMode = true;
    private Lexer _lexer = new Lexer();
    private SymbolTable _symbolTable;
    private boolean _showDisassembly;
    private VirtualMachine _vm;
    private Document _document;
    private boolean _quit;
    private Caret _caret;

    private void prompt() {
        _document.carriageReturn();
        var buffer = new StringBuilder();
        if (_vm.isStackEmpty())
            buffer.append("(empty):0");
        else {
            var tos = _vm.topOfStack();
            buffer.append("(");
            buffer.append(String.format("%016X", tos));
            buffer.append(":");
            buffer.append(_vm.getStackDepth());
            buffer.append(")");
        }
        _document.writeAtCaret(buffer.toString());
        _document.writeAtCaret(" > ");

        var cx = _caret.getX();
        var cy = _caret.getY();
        _caret.setLimit(new Bounds(cx, cy, _document.getDisplayWidth(), cy));
    }

    private boolean processTraps(Result r) {
        for (var capture : _deferredTraps) {
            if (!capture.handler.execute(r, capture.context))
                return false;
        }
        _deferredTraps.clear();
        return true;
    }

    private void prevHistoryCommand() {
        var limit = _caret.getLimit();
        _document.clearRegion(limit);
        _caret.moveTo(limit.top, limit.left);
        var command = _history.prevCommand();
        if (command == null)
            return;
        _document.writeAtCaret(command);
    }

    private void nextHistoryCommand() {
        var limit = _caret.getLimit();
        _document.clearRegion(limit);
        _caret.moveTo(limit.top, limit.left);
        var command = _history.nextCommand();
        if (command == null)
            return;
        _document.writeAtCaret(command);
    }

    private void displayOptionsBar() {
        var panel = _document.getPanel();
        var showDisassembly = _showDisassembly ? "Hide Disassembly" : "Show Disassembly";
        panel.write(
                StringHelpers.padString(
                        String.format(
                                " ESC=Quit \u00b3 F1=%s \u00b3 F2=Symbols \u00b3 F3=Registers \u00b3 F4=Stack \u00b3 F5=Clear | INS=Overwrite",
                                showDisassembly),
                        130),
                0,
                Constants.Terminal.HEIGHT,
                Color.WHITE,
                Color.DARK_GRAY);
    }

    private void displayBanner() {
        _document.writeAtCaret("                                                                                                                                  " +
                        "                               ____  ____   __    ___  ____   __   _  _  _  _  ____  ____  ____  _                                " +
                        "                              (  _ \\(  _ \\ /  \\  / __)(  _ \\ / _\\ ( \\/ )( \\/ )(  __)(  _ \\/ ___)(/                                " +
                        "                               ) __/ )   /(  O )( (_ \\ )   //    \\/ \\/ \\/ \\/ \\ ) _)  )   /\\___ \\                                  " +
                        "                              (__)  (__\\_) \\__/  \\___/(__\\_)\\_/\\_/\\_)(_/\\_)(_/(____)(__\\_)(____/                                  " +
                        "                                        ___   __   __     ___  _  _  __     __  ____  __  ____                                    " +
                        "                                       / __) / _\\ (  )   / __)/ )( \\(  )   / _\\(_  _)/  \\(  _ \\                                   " +
                        "                                      ( (__ /    \\/ (_/\\( (__ ) \\/ (/ (_/\\/    \\ )( (  O ))   /                                   " +
                        "                                       \\___)\\_/\\_/\\____/ \\___)\\____/\\____/\\_/\\_/(__) \\__/(__\\_)                                   " +
                        "                                                                                                                                  ",
                s_gradientColors[2],
                Color.YELLOW);
        _document.writeAtCaret(
                padString(String.format(
                        "                                 Version: %s, %s",
                        Constants.PROGRAM_VERSION,
                        Constants.COPYRIGHT_NOTICE), 130),
                s_gradientColors[1],
                Color.WHITE);
        _document.writeAtCaret(padString("", 130) + "\n", s_gradientColors[0], Color.WHITE);
    }

    private void clearScreen() {
        clearScreen(true);
    }

    private void clearScreen(boolean showPrompt) {
        _caret.setLimit(null);
        _document.clear();
        displayBanner();
        displayOptionsBar();
        if (showPrompt)
            prompt();
    }

    private void formatDisassembly(Program program) {
        _document.carriageReturn();
        formatGradientTop();
        var lines = program.disassemble();
        for (var line : lines)
            _document.writeAtCaret(padString(String.format(" %s", line), 130), s_gradientColors[2], Color.CYAN);
        formatGradientBottom();
    }

    private void formatGradientTop() {
        var paddedString = padString("", 130);
        _document.writeAtCaret(paddedString, s_gradientColors[0], Color.CYAN);
        _document.writeAtCaret(paddedString, s_gradientColors[1], Color.CYAN);
    }

    private void formatGradientBottom() {
        var paddedString = padString("", 130);
        _document.writeAtCaret(paddedString, s_gradientColors[1], Color.CYAN);
        _document.writeAtCaret(paddedString, s_gradientColors[0], Color.CYAN);
    }

    private void formatOutput(String typeName, long value) {
        _document.carriageReturn();
        formatGradientTop();
        _document.writeAtCaret(
                padString(String.format(" Type: %s", typeName), 130),
                s_gradientColors[2],
                Color.CYAN);
        _document.writeAtCaret(
                padString(" BYTE      WORD              DWORD                             QWORD", 130),
                s_gradientColors[2],
                Color.CYAN);
        _document.writeAtCaret(
                String.format(" %%%s %%%s %%%s %%%s  ",
                    NumberHelpers.binaryStringForSize(value, 8),
                    NumberHelpers.binaryStringForSize(value, 16),
                    NumberHelpers.binaryStringForSize(value, 32),
                    NumberHelpers.binaryStringForSize(value, 64)),
                s_gradientColors[2],
                Color.CYAN);
        _document.writeAtCaret(
                String.format(" $%s $%s $%s $%s  ",
                    NumberHelpers.hexStringForSize(value, 8),
                    NumberHelpers.hexStringForSize(value, 16),
                    NumberHelpers.hexStringForSize(value, 32),
                    NumberHelpers.hexStringForSize(value, 64)),
                s_gradientColors[2],
                Color.CYAN);
        _document.writeAtCaret(
                String.format(" @%s @%s @%s @%s  ",
                    NumberHelpers.octalStringForSize(value, 8),
                    NumberHelpers.octalStringForSize(value, 16),
                    NumberHelpers.octalStringForSize(value, 32),
                    NumberHelpers.octalStringForSize(value, 64)),
                s_gradientColors[2],
                Color.CYAN);
        formatGradientBottom();
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

        if (_showDisassembly)
            formatDisassembly(compiledProgram);

        _vm.setProgram(compiledProgram);
        if (!_vm.run(r))
            return null;
        var targetRegister = compiledProgram.getTargetRegister();
        if (targetRegister == null)
            return null;
        return _vm.getRegister(targetRegister);
    }

    private void endHandleExpression() {
        if (_quit) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else {
            prompt();
        }
    }

    private void handleExpression() {
        var r = new Result();

        var source = _document.getString(_caret.getLimit());
        _caret.setLimit(null);
        _document.carriageReturn();

        if (source.isEmpty()) {
            endHandleExpression();
            return;
        }

        _history.addCommand(source);

        _stopWatch.start();
        var targetRegister = executeExpression(r, source);
        if (!r.isSuccess() || targetRegister == null) {
            formatResult(r);
            endHandleExpression();
            return;
        }

        var elapsedTime = _stopWatch.stop();

        _document.carriageReturn();

        var type = targetRegister.getType().getNumericType();
        var typeName = switch (type) {
            case Integer -> "U64";
            case Float   -> "F64";
        };
        var value = switch (type) {
            case Integer -> targetRegister.getValue().asInteger();
            case Float   -> Double.doubleToRawLongBits(targetRegister.getValue().asFloat());
        };

        formatGradientTop();

        switch (targetRegister.getType()) {
            case U0, U64 -> _document.writeAtCaret(
                    padString(String.format("   result value: %d:%s", value, typeName), 130),
                    s_gradientColors[2],
                    Color.CYAN);
            case F64     -> _document.writeAtCaret(
                    padString(
                            String.format("   result value: %f:%s", targetRegister.getValue().asFloat(), typeName),
                            130),
                    s_gradientColors[2],
                    Color.CYAN);
        }

        String timeValue = "";
        var milliSeconds = elapsedTime.toMillis();
        if (milliSeconds > 0) {
            timeValue = String.format("%dms", milliSeconds);
        } else {
            var nanoSeconds = elapsedTime.toNanos();
            timeValue = String.format("%dus", nanoSeconds / 1000L);
        }

        _document.writeAtCaret(
                padString(String.format(" execution time: %s", timeValue), 130),
                s_gradientColors[2],
                Color.CYAN);
        formatGradientBottom();

        processTraps(r);

        endHandleExpression();
    }

    private void formatResult(Result result) {
        for (var msg : result.getMessages()) {
            var color = msg.getType() == ResultMessageType.Error ? Color.ORANGE : Color.PINK;
            var formatted = String.format(
                    "[%s] %s\n",
                    msg.getCode(),
                    msg.getMessage());
            _document.writeAtCaret(formatted, Color.BLUE, color);

            var detail = msg.getDetails();
            if (detail != null && detail.length() > 0) {
                _document.writeAtCaret(String.format("       %s\n", detail), Color.BLUE, color);
            }
        }
    }

    public GuiShell(Configuration configuration) {
        super();
        _configuration = configuration;
    }

    @Override
    public void run() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setResizable(false);
        setTitle("Programmers' Calculator");

        setLocationRelativeTo(null);
        setVisible(true);

        prompt();
    }

    @Override
    public boolean initialize(Result r) {
        _document = new Document(
                Constants.Terminal.HEIGHT,
                Constants.Terminal.WIDTH,
                Constants.Terminal.HEIGHT,
                Constants.Terminal.WIDTH);
        _caret = new Caret(_document, 0, 0, e -> repaint());

        var size = _document.getDimensions();
        setPreferredSize(size);
        setLayout(new BorderLayout());
        add(_layeredPane, BorderLayout.CENTER);
        var panel = _document.getPanel();
        panel.setBounds(0, 0, size.width, size.height);
        _caret.setBounds(0, 0, size.width, size.height);
        _layeredPane.add(panel, 0, 0);
        _layeredPane.add(_caret, 1, 0);
        pack();
        addKeyListener(this);

        _vm = new VirtualMachine(65535, 4096, 4096);
        if (!_vm.initialize(r))
            return false;
        _symbolTable = new SymbolTable(_vm);

        _vm.addTrapHandler(
                Constants.Traps.QUIT,
                (result, vm) -> {
                    _quit = true;
                    return true;
                });
        _vm.addTrapHandler(
                Constants.Traps.POP,
                (result, vm) -> {
                    if (vm.isStackEmpty()) {
                        result.addError(
                                "T001",
                                "POP trap expects arguments on stack.");
                        return false;
                    }
                    var capture = new TrapCapture();
                    capture.context.trapId = Constants.Traps.POP;
                    capture.context.values.add(new IntegerNumeric(vm.pop()));
                    capture.handler = (handlerResult, context) -> {
                        formatOutput("U64", context.values.get(0).asInteger());
                        return true;
                    };
                    _deferredTraps.add(capture);
                    return true;
                });
        _vm.addTrapHandler(
                Constants.Traps.PUSH,
                (result, vm) -> {
                    var capture = new TrapCapture();
                    capture.context.trapId = Constants.Traps.PUSH;
                    var numericTypes = NumericType.values();
                    var type = numericTypes[(int) vm.pop()];
                    var value = switch (type) {
                        case Integer -> vm.pop();
                        case Float   -> Double.doubleToRawLongBits(vm.popDouble());
                    };
                    capture.context.values.add(new IntegerNumeric(value));
                    capture.handler = (handlerResult, context) -> {
                        vm.push(context.values.get(0));
                        return true;
                    };
                    _deferredTraps.add(capture);
                    return true;
                });
        _vm.addTrapHandler(
                Constants.Traps.CLEAR_SCREEN,
                (result, vm) -> {
                    var capture = new TrapCapture();
                    capture.context.trapId = Constants.Traps.CLEAR_SCREEN;
                    capture.handler = (handlerResult, context) -> {
                        clearScreen(false);
                        return true;
                    };
                    _deferredTraps.add(capture);
                    return true;
                });
        _vm.addTrapHandler(
                io.nybbles.progcalc.vm.Constants.Traps.PRINT,
                (result, vm) -> {
                    if (vm.isStackEmpty()) {
                        result.addError(
                                "T001",
                                "PRINT trap expects arguments on stack.");
                        return false;
                    }

                    var capture = new TrapCapture();
                    capture.context.trapId = io.nybbles.progcalc.vm.Constants.Traps.PRINT;

                    var numericTypes = NumericType.values();
                    var count = vm.pop();
                    for (var i = 0; i < count; i++) {
                        var type = numericTypes[(int) vm.pop()];
                        var value = switch (type) {
                            case Integer -> vm.pop();
                            case Float   -> Double.doubleToRawLongBits(vm.popDouble());
                        };
                        capture.context.values.add(new IntegerNumeric(value));
                    }

                    capture.handler = (handleResult, context) -> {
                        for (var value : context.values) {
                            var typeName = switch (value.getType()) {
                                case Integer -> "U64";
                                case Float   -> "F64";
                            };
                            formatOutput(typeName, value.asInteger());
                        }
                        return true;
                    };

                    _deferredTraps.add(capture);
                    return true;
                });

        displayBanner();
        displayOptionsBar();

        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        var ch = e.getKeyChar();
        if (ch < 255) {
            switch (ch) {
                case '\n' -> handleExpression();
                case 8    -> _document.backSpaceAtCaret();
                case 9    -> _document.tab();
                case 127  -> _document.deleteAtCaret();
                default -> {
                    if (_insertMode)
                        _document.insertAtCaret(ch);
                    else
                        _document.writeAtCaret(ch);
                }
            }
        }
        super.repaint();
    }

    private void toggleInsertMode() {
        var panel = _document.getPanel();
        _insertMode = !_insertMode;
        if (_insertMode) {
            panel.write("Overwrite", 88, Constants.Terminal.HEIGHT, Color.WHITE, Color.DARK_GRAY);
        } else {
            panel.write("Insert   ", 88, Constants.Terminal.HEIGHT, Color.WHITE, Color.DARK_GRAY);
        }
    }

    private void toggleShowDisassembly() {
        var panel = _document.getPanel();
        _showDisassembly = !_showDisassembly;
        if (_showDisassembly) {
            panel.write("Hide", 15, Constants.Terminal.HEIGHT, Color.WHITE, Color.DARK_GRAY);
        } else {
            panel.write("Show", 15, Constants.Terminal.HEIGHT, Color.WHITE, Color.DARK_GRAY);
        }
    }

    private void formatSymbolTable() {
        _caret.setLimit(null);
        _document.carriageReturn();

        formatGradientTop();
        var names = _symbolTable.getSymbols();
        if (names.isEmpty()) {
            _document.writeAtCaret(
                    padString(" (symbol table empty)", 130),
                    s_gradientColors[2],
                    Color.CYAN);
        } else {
            _document.writeAtCaret(
                    padString(" SYMBOL                           TYPE QWORD", 130),
                    s_gradientColors[2],
                    Color.CYAN);

            for (var name : names) {
                var symbol = _symbolTable.getSymbol(name);
                var type = _symbolTable.getSymbolType(name);
                var typeName = switch (type) {
                    case Integer -> "U64";
                    case Float   -> "F64";
                };
                var value = switch (type) {
                    case Integer -> symbol.asInteger();
                    case Float   -> Double.doubleToRawLongBits(symbol.asFloat());
                };
                _document.writeAtCaret(
                        padString(
                                String.format(
                                        " %-32s %-4s $%016x",
                                        name,
                                        typeName,
                                        value),
                                130),
                        s_gradientColors[2],
                        Color.CYAN);
            }
        }
        formatGradientBottom();

        prompt();
    }

    private void formatRegisters() {
        _caret.setLimit(null);
        _document.carriageReturn();

        formatGradientTop();
        _document.writeAtCaret(
                padString(" REG TYPE QWORD", 130),
                s_gradientColors[2],
                Color.CYAN);
        for (var registerName : RegisterName.values()) {
            if (registerName == RegisterName.NN)
                continue;
            var register = _vm.getRegister(registerName);
            var type = register.getType().getNumericType();
            var typeName = switch (type) {
                case Integer -> "U64";
                case Float   -> "F64";
            };
            var value = switch (type) {
                case Integer -> register.getValue().asInteger();
                case Float   -> Double.doubleToRawLongBits(register.getValue().asFloat());
            };
            _document.writeAtCaret(
                    padString(
                            String.format(
                                    " %-3s %-4s $%016x",
                                    registerName.name(),
                                    typeName,
                                    value),
                            130),
                    s_gradientColors[2],
                    Color.CYAN);
        }
        formatGradientBottom();

        prompt();
    }

    private void formatStack() {
        _caret.setLimit(null);
        _document.carriageReturn();

        formatGradientTop();
        if (_vm.isStackEmpty()) {
            _document.writeAtCaret(
                    padString(" (stack empty)", 130),
                    s_gradientColors[2],
                    Color.CYAN);
        } else {
            _document.writeAtCaret(
                    padString(" ADDR              QWORD", 130),
                    s_gradientColors[2],
                    Color.CYAN);
            var sp = _vm.getRegister(RegisterName.SP).getValue().asInteger();
            for (var i = 0; i < _vm.getStackDepth(); ++i) {
                _document.writeAtCaret(
                        padString(
                                String.format(
                                        " $%016x $%016x",
                                        sp,
                                        _vm.getHeapLong(sp)),
                                130),
                        s_gradientColors[2],
                        Color.CYAN);
                sp += Long.BYTES;
            }
        }
        formatGradientBottom();

        prompt();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F1     -> toggleShowDisassembly();
            case KeyEvent.VK_F2     -> formatSymbolTable();
            case KeyEvent.VK_F3     -> formatRegisters();
            case KeyEvent.VK_F4     -> formatStack();
            case KeyEvent.VK_F5     -> clearScreen();
            case KeyEvent.VK_UP     -> prevHistoryCommand();
            case KeyEvent.VK_DOWN   -> nextHistoryCommand();
            case KeyEvent.VK_HOME   -> _caret.moveHome();
            case KeyEvent.VK_END    -> _caret.moveEnd();
            case KeyEvent.VK_LEFT   -> _caret.moveLeft();
            case KeyEvent.VK_RIGHT  -> _caret.moveRight();
            case KeyEvent.VK_INSERT -> toggleInsertMode();
            case KeyEvent.VK_ESCAPE -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        super.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
