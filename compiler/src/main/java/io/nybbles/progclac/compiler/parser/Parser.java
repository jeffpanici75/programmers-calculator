package io.nybbles.progclac.compiler.parser;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progclac.compiler.ast.*;
import io.nybbles.progclac.compiler.lexer.Token;
import io.nybbles.progclac.compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private HashMap<TokenType, ProductionRule> _ruleTable = new HashMap<>();
    private ArrayList<ProductionRule> _rules = new ArrayList<>();
    private ArrayList<Token> _tokens;
    private String _source;
    private int _index;

    private static FloatSize narrowType(double value) {
        if (value < -3.4e+38 || value > 3.4e+38) {
            return FloatSize.QWORD;
        } else if (value >= -3.4e+38 && value <= 3.4e+38) {
            return FloatSize.DWORD;
        } else {
            return FloatSize.INVALID;
        }
    }
    private static IntegerSize narrowType(long value) {
        var highestBit = 0;
        for (var i = 0; i < Long.SIZE; ++i) {
            var mask = 1L << i;
            if ((value & mask) == mask)
                highestBit = i;
        }

        var sizeInBytes = (highestBit / 8) + 1;

        return switch (sizeInBytes) {
            case 1 -> IntegerSize.BYTE;
            case 2 -> IntegerSize.WORD;
            case 4 -> IntegerSize.DWORD;
            default -> IntegerSize.QWORD;
        };
    }

    private Token token() {
        return _tokens.get(_index);
    }

    private boolean hasMore() {
        return _index < _tokens.size()
            && _rules.get(_index).getTokenType() != TokenType.EndOfInput;
    }

    private ProductionRule rule() {
        return _rules.get(_index);
    }

    private boolean apply(Result r) {
        for (var token : _tokens) {
            var s = _ruleTable.getOrDefault(token.getType(), null);
            if (s == null) {
                addError(
                        r,
                        "P008",
                        String.format("no production rule for token: %s", token.getType()),
                        token.getStart(),
                        token.getEnd());
                return false;
            }
            _rules.add(s);
        }
        return true;
    }

    private boolean peek(TokenType tokenType) {
        return token().getType() == tokenType;
    }

    private boolean expect(Result r, TokenType tokenType) {
        var t = token();
        if (t.getType() != tokenType) {
            addError(
                    r,
                "P016",
                    String.format(
                        "expected token %s but found %s",
                        tokenType,
                        t.getType()),
                    t.getStart(),
                    t.getEnd());
            return false;
        }
        return true;
    }

    private boolean advance(Result r) {
        return advance(r, null);
    }

    private boolean advance(Result r, TokenType tokenType) {
        if (tokenType != null && !expect(r, tokenType))
            return false;

        if (_index < _tokens.size()) {
            ++_index;
            return true;
        }

        return false;
    }

    private ProductionRule infix(TokenType tokenType, int bp) {
        return infix(tokenType, bp, null);
    }

    private void addError(Result r, String code, String message, int start, int end) {
        var underline = "^";
        var length = (end - start) - 1;
        if (length > 0) {
            underline += "~".repeat(length) + ">";
        }
        var indicator = String.format(
                "%s %s",
                underline,
                message);
        String buffer = _source +
                "\n" +
                String.format("%" + (start + 7 + indicator.length()) + "s", indicator);
        r.addError(code, buffer);
    }

    private ProductionRule infix(TokenType tokenType, int bp, Led led) {
        var s = terminal(tokenType, bp);
        if (led != null) {
            s.setLed(led);
        } else {
            s.setLed((context, lhs) -> {
                var operatorType = BinaryOperatorType.fromTokenType(context.token.getType());
                if (operatorType == null) {
                    context.parser.addError(
                            context.r,
                            "P002",
                            "invalid binary operator type",
                            context.token.getStart(),
                            context.token.getEnd());
                    return null;
                }
                if (!context.parser.peek(TokenType.NumberLiteral)
                &&  !context.parser.peek(TokenType.BooleanLiteral)
                &&  !context.parser.peek(TokenType.CharacterLiteral)
                &&  !context.parser.peek(TokenType.Identifier)
                &&  !context.parser.peek(TokenType.LeftParen)
                &&  !context.parser.peek(TokenType.Minus)
                &&  !context.parser.peek(TokenType.Tilde)) {
                    context.parser.addError(
                            context.r,
                            "P023",
                            "binary operator rhs expected: number, boolean, character, identifier, or expression.",
                            context.token.getStart(),
                            context.token.getEnd());
                    return null;
                }
                return new BinaryOperatorAstNode(
                        operatorType,
                        lhs,
                        context.parser.expression(
                                context.r,
                                context.rule.getLbp()),
                        context.token);
            });
        }
        return s;
    }

    private ProductionRule prefix(TokenType tokenType) {
        return prefix(tokenType, null);
    }

    private ProductionRule prefix(TokenType tokenType, Nud nud) {
        var s = terminal(tokenType, 0);
        if (nud != null) {
            s.setNud(nud);
        } else {
            s.setNud((context) -> {
                var operatorType = UnaryOperatorType.fromTokenType(context.token.getType());
                if (operatorType == null) {
                    context.parser.addError(
                            context.r,
                            "P001",
                            "invalid unary operator type",
                            context.token.getStart(),
                            context.token.getEnd());
                    return null;
                }
                if (!context.parser.peek(TokenType.NumberLiteral)
                &&  !context.parser.peek(TokenType.BooleanLiteral)
                &&  !context.parser.peek(TokenType.CharacterLiteral)
                &&  !context.parser.peek(TokenType.Identifier)
                &&  !context.parser.peek(TokenType.LeftParen)
                &&  !context.parser.peek(TokenType.Minus)
                &&  !context.parser.peek(TokenType.Tilde)) {
                    context.parser.addError(
                            context.r,
                            "P023",
                            "unary operator expected: number, boolean, character, identifier, or expression.",
                            context.token.getStart(),
                            context.token.getEnd());
                    return null;
                }
                return new UnaryOperatorAstNode(
                        operatorType,
                        context.parser.expression(context.r, 80),
                        context.token);
            });
        }
        return s;
    }

    private ProductionRule postfix(TokenType tokenType, int bp) {
        return postfix(tokenType, bp, null);
    }

    private ProductionRule postfix(TokenType tokenType, int bp, Led led) {
        var s = terminal(tokenType, bp);
        if (led != null) {
            s.setLed(led);
        } else {
            s.setLed((context, lhs) -> {
                var operatorType = UnaryOperatorType.fromTokenType(context.token.getType());
                if (operatorType == null) {
                    context.parser.addError(
                            context.r,
                            "P001",
                            "invalid unary operator type",
                            context.token.getStart(),
                            context.token.getEnd());
                    return null;
                }
                return new UnaryOperatorAstNode(
                        operatorType,
                        lhs,
                        context.token);
            });
        }
        return s;
    }

    private ProductionRule terminal(TokenType tokenType) {
        return terminal(tokenType, 0);
    }

    private ProductionRule terminal(TokenType tokenType, int bp) {
        var s = _ruleTable.getOrDefault(tokenType, null);
        if (s != null) {
            if (bp >= s.getLbp())
                s.setLbp(bp);
        } else {
            s = new ProductionRule(tokenType);
            s.setLbp(bp);
            s.setNud(context -> {
                context.parser.addError(
                        context.r,
                        "P006",
                        "missing production rule",
                        context.token.getStart(),
                        context.token.getEnd());
                return null;
            });
            s.setLed((context, lhs) -> {
                context.parser.addError(
                        context.r,
                        "P006",
                        "missing production rule",
                        context.token.getStart(),
                        context.token.getEnd());
                return null;
            });
            _ruleTable.put(tokenType, s);
        }
        return s;
    }

    private ProductionRule statement(TokenType tokenType, Std std) {
        var s = terminal(tokenType);
        s.setStd(std);
        return s;
    }

    private ProductionRule infixRight(TokenType tokenType, int bp) {
        return infixRight(tokenType, bp, null);
    }

    private ProductionRule infixRight(TokenType tokenType, int bp, Led led) {
        var s = terminal(tokenType, bp);
        if (led != null) {
            s.setLed(led);
        } else {
            s.setLed((context, lhs) -> {
                var operatorType = BinaryOperatorType.fromTokenType(context.token.getType());
                if (operatorType == null) {
                    context.parser.addError(
                            context.r,
                            "P002",
                            "invalid binary operator type",
                            context.token.getStart(),
                            context.token.getEnd());
                    return null;
                }
                return new BinaryOperatorAstNode(
                        operatorType,
                        lhs,
                        context.parser.expression(
                                context.r,
                                context.rule.getLbp() - 1),
                        context.token);
            });
        }
        return s;
    }

    private StatementAstNode parseStatement(Result r) {
        if (!hasMore())
            return null;

        var currentRule = rule();
        var currentToken = token();

        var std = currentRule.getStd();
        if (std != null) {
            var context = new Context();
            context.r = r;
            context.parser = this;
            context.rule = currentRule;
            context.token = currentToken;
            if (!advance(r))
                return null;
            return std.parse(context);
        }

        var expression = expression(r, 0);
        return new StatementAstNode(expression);
    }

    private ArrayList<StatementAstNode> parseStatements(Result r) {
        var statements = new ArrayList<StatementAstNode>();
        while (hasMore()) {
            var stmt = parseStatement(r);
            if (stmt == null)
                break;
            statements.add(stmt);
            if (!peek(TokenType.Semicolon) && !hasMore())
                break;
            if (!advance(r, TokenType.Semicolon))
                return null;
        }
        return statements;
    }

    public Parser(String source, ArrayList<Token> tokens) {
        _tokens = tokens;
        _source = source;
    }

    public AstNode parse(Result r) {
        var statements = parseStatements(r);
        if (!r.isSuccess())
            return null;
        return new ProgramAstNode(statements);
    }

    public boolean initialize(Result r) {
        terminal(TokenType.Semicolon);
        terminal(TokenType.RightParen);
        terminal(TokenType.EndOfInput);
        terminal(TokenType.RightBracket);

        prefix(TokenType.Minus);
        prefix(TokenType.Tilde);

        statement(
                TokenType.Pop,
                (context) -> {
                    if (!context.parser.peek(TokenType.Semicolon)
                    &&  !context.parser.peek(TokenType.EndOfInput)) {
                        addError(
                                context.r,
                                "P077",
                                "unexpected expression after pop command",
                                context.token.getStart(),
                                context.token.getEnd());
                        return null;
                    }
                    var commandType = CommandType.fromTokenType(context.token.getType());
                    return new StatementAstNode(new CommandAstNode(
                            commandType,
                            new IntegerLiteralAstNode(1L, 10, false, IntegerSize.BYTE, null),
                            context.token));
                });

        statement(
                TokenType.Push,
                (context) -> {
                    var commandType = CommandType.fromTokenType(context.token.getType());
                    return new StatementAstNode(new CommandAstNode(
                            commandType,
                            context.parser.expression(context.r, 0),
                            context.token));
                });

        statement(
                TokenType.Clear,
                (context) -> {
                    if (!context.parser.peek(TokenType.Semicolon)
                            &&  !context.parser.peek(TokenType.EndOfInput)) {
                        addError(
                                context.r,
                                "P077",
                                "unexpected expression after pop command",
                                context.token.getStart(),
                                context.token.getEnd());
                        return null;
                    }
                    var commandType = CommandType.fromTokenType(context.token.getType());
                    return new StatementAstNode(new CommandAstNode(
                            commandType,
                            new IntegerLiteralAstNode(1L, 10, false, IntegerSize.BYTE, null),
                            context.token));
                });

        statement(
                TokenType.Question,
                (context) -> {
                    var expr = context.parser.expression(context.r, 0);
                    if (expr == null) {
                        addError(
                                context.r,
                                "P076",
                                "? command expected valid expression",
                                context.token.getStart(),
                                context.token.getEnd());
                        return null;
                    }
                    var commandType = CommandType.fromTokenType(context.token.getType());
                    return new StatementAstNode(new CommandAstNode(
                            commandType,
                            expr,
                            context.token));
                });

        prefix(
                TokenType.CharacterLiteral,
                (context) -> new CharacterLiteralAstNode(
                        context.token.getSlice().charAt(0),
                        context.token));
        prefix(
                TokenType.BooleanLiteral,
                (context) -> new BooleanLiteralAstNode(
                        context.token.getSlice().equals("true"),
                        context.token));

        infixRight(TokenType.Caret, 75);

        infix(TokenType.Plus, 50);
        infix(TokenType.Minus, 50);

        infix(TokenType.Percent, 60);
        infix(TokenType.Asterisk, 60);
        infix(TokenType.ForwardSlash, 60);

        infix(TokenType.Xor, 70);
        infix(TokenType.Shl, 70);
        infix(TokenType.Shr, 70);
        infix(TokenType.Rol, 70);
        infix(TokenType.Ror, 70);
        infix(TokenType.Pipe, 70);
        infix(TokenType.Ampersand, 70);

        infix(TokenType.Equals, 40);
        infix(TokenType.LessThan, 40);
        infix(TokenType.NotEquals, 40);
        infix(TokenType.GreaterThan, 40);
        infix(TokenType.LessThanEquals, 40);
        infix(TokenType.GreaterThanEquals, 40);

        infix(
            TokenType.Assign,
            10,
            (context, lhs) -> {
                if (lhs.getType() != AstNodeType.Identifier) {
                    context.parser.addError(
                            context.r,
                            "P021",
                            "assignment lvalue must be an identifier.",
                            context.token.getStart(),
                            context.token.getEnd());
                    return null;
                }
                return new AssignmentAstNode(
                        lhs,
                        context.parser.expression(
                                context.r,
                                context.rule.getLbp()),
                        context.token);
            });

        infixRight(TokenType.LogicalOr, 30);
        infixRight(TokenType.LogicalAnd, 30);

        prefix(
            TokenType.LeftParen,
            (context) -> {
                var expr = context.parser.expression(context.r, 0);
                if (!context.parser.advance(context.r, TokenType.RightParen))
                    return null;
                return expr;
            });

        prefix(
            TokenType.Identifier,
            (context) -> new IdentifierAstNode(context.token.getSlice(), context.token));

        prefix(
            TokenType.NumberLiteral,
            (context) -> {
                var slice = context.token.getSlice();
                var radix = context.token.getRadix();
                try {
                    if (context.token.isFractional()) {
                        var value = Double.parseDouble(slice);
                        return new DoubleLiteralAstNode(
                                value,
                                narrowType(value),
                                context.token);
                    } else {
                        var value = Long.parseLong(slice, radix);
                        return new IntegerLiteralAstNode(
                                value,
                                radix,
                                false,
                                narrowType(value),
                                context.token);
                    }
                } catch (NumberFormatException e) {
                    context.parser.addError(
                            context.r,
                            "P041",
                            String.format("invalid number for radix: %d", radix),
                            context.token.getStart(),
                            context.token.getEnd());
                    return null;
                }
            });

        return apply(r);
    }

    public AstNode expression(Result r, int rbp) {
        if (!hasMore())
            return null;

        var currentRule = rule();
        var currentToken = token();

        var context = new Context();
        context.r = r;
        context.parser = this;
        context.rule = currentRule;
        context.token = currentToken;

        if (!advance(r))
            return null;

        var lhs = currentRule.getNud().parse(context);
        if (lhs == null)
            return null;

        var nextRule = rule();
        while (rbp < nextRule.getLbp()) {
            context.token = token();
            context.rule = nextRule;
            if (!advance(r))
                return null;
            lhs = nextRule.getLed().parse(context, lhs);
            nextRule = rule();
        }

        return lhs;
    }
}
