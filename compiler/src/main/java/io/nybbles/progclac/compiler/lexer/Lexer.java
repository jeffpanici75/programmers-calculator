package io.nybbles.progclac.compiler.lexer;

import io.nybbles.progcalc.adt.TrieMap;
import io.nybbles.progcalc.common.Result;

import java.util.ArrayList;
import java.util.Stack;

public class Lexer {
    private int _index;
    private String _buffer;
    private static TrieMap<Lexeme> s_lexemes = new TrieMap<>();

    static {
        s_lexemes.insert("|", new Lexeme(TokenType.Pipe));
        s_lexemes.insert("+", new Lexeme(TokenType.Plus));
        s_lexemes.insert("~", new Lexeme(TokenType.Tilde));
        s_lexemes.insert("-", new Lexeme(TokenType.Minus));
        s_lexemes.insert("^", new Lexeme(TokenType.Caret));
        s_lexemes.insert(":=", new Lexeme(TokenType.Assign));
        s_lexemes.insert("==", new Lexeme(TokenType.Equals));
        s_lexemes.insert("%", new Lexeme(TokenType.Percent));
        s_lexemes.insert("*", new Lexeme(TokenType.Asterisk));
        s_lexemes.insert("<", new Lexeme(TokenType.LessThan));
        s_lexemes.insert("?", new Lexeme(TokenType.Question));
        s_lexemes.insert("&", new Lexeme(TokenType.Ampersand));
        s_lexemes.insert("(", new Lexeme(TokenType.LeftParen));
        s_lexemes.insert(";", new Lexeme(TokenType.Semicolon));
        s_lexemes.insert(")", new Lexeme(TokenType.RightParen));
        s_lexemes.insert("\\", new Lexeme(TokenType.BackSlash));
        s_lexemes.insert("!=", new Lexeme(TokenType.NotEquals));
        s_lexemes.insert("||", new Lexeme(TokenType.LogicalOr));
        s_lexemes.insert(">", new Lexeme(TokenType.GreaterThan));
        s_lexemes.insert("[", new Lexeme(TokenType.LeftBracket));
        s_lexemes.insert("&&", new Lexeme(TokenType.LogicalAnd));
        s_lexemes.insert("]", new Lexeme(TokenType.RightBracket));
        s_lexemes.insert("/", new Lexeme(TokenType.ForwardSlash));
        s_lexemes.insert("<=", new Lexeme(TokenType.LessThanEquals));
        s_lexemes.insert(">=", new Lexeme(TokenType.GreaterThanEquals));
        s_lexemes.insert("xor", new Lexeme(TokenType.Xor, true));
        s_lexemes.insert("XOR", new Lexeme(TokenType.Xor, true));
        s_lexemes.insert("shl", new Lexeme(TokenType.Shl, true));
        s_lexemes.insert("SHL", new Lexeme(TokenType.Shl, true));
        s_lexemes.insert("shr", new Lexeme(TokenType.Shr, true));
        s_lexemes.insert("SHR", new Lexeme(TokenType.Shr, true));
        s_lexemes.insert("rol", new Lexeme(TokenType.Rol, true));
        s_lexemes.insert("ROL", new Lexeme(TokenType.Rol, true));
        s_lexemes.insert("ror", new Lexeme(TokenType.Ror, true));
        s_lexemes.insert("ROR", new Lexeme(TokenType.Ror, true));

        s_lexemes.insert("true", new Lexeme(TokenType.BooleanLiteral, true));
        s_lexemes.insert("TRUE", new Lexeme(TokenType.BooleanLiteral, true));
        s_lexemes.insert("false", new Lexeme(TokenType.BooleanLiteral, true));
        s_lexemes.insert("FALSE", new Lexeme(TokenType.BooleanLiteral, true));

        s_lexemes.insert("_", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("a", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("A", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("b", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("B", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("c", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("C", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("d", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("D", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("e", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("E", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("f", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("F", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("g", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("G", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("h", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("H", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("i", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("I", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("j", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("J", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("k", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("K", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("l", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("L", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("m", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("M", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("n", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("N", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("o", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("O", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("p", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("P", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("q", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("Q", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("r", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("R", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("s", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("S", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("t", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("T", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("u", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("U", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("v", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("V", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("w", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("W", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("x", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("X", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("y", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("Y", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("z", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));
        s_lexemes.insert("Z", new Lexeme(TokenType.Identifier, Lexer::identifierLiteral));

        s_lexemes.insert("0", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("1", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("2", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("3", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("4", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("5", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("6", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("7", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("8", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));
        s_lexemes.insert("9", new Lexeme(TokenType.NumberLiteral, Lexer::decimalIntegerLiteral));

        s_lexemes.insert("@0", new Lexeme(TokenType.NumberLiteral, Lexer::octalIntegerLiteral));
        s_lexemes.insert("@1", new Lexeme(TokenType.NumberLiteral, Lexer::octalIntegerLiteral));
        s_lexemes.insert("@2", new Lexeme(TokenType.NumberLiteral, Lexer::octalIntegerLiteral));
        s_lexemes.insert("@3", new Lexeme(TokenType.NumberLiteral, Lexer::octalIntegerLiteral));
        s_lexemes.insert("@4", new Lexeme(TokenType.NumberLiteral, Lexer::octalIntegerLiteral));
        s_lexemes.insert("@5", new Lexeme(TokenType.NumberLiteral, Lexer::octalIntegerLiteral));
        s_lexemes.insert("@6", new Lexeme(TokenType.NumberLiteral, Lexer::octalIntegerLiteral));
        s_lexemes.insert("@7", new Lexeme(TokenType.NumberLiteral, Lexer::octalIntegerLiteral));

        s_lexemes.insert("%0", new Lexeme(TokenType.NumberLiteral, Lexer::binaryIntegerLiteral));
        s_lexemes.insert("%1", new Lexeme(TokenType.NumberLiteral, Lexer::binaryIntegerLiteral));

        s_lexemes.insert("$0", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$1", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$2", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$3", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$4", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$5", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$6", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$7", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$8", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$9", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$a", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$A", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$b", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$B", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$c", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$C", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$d", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$D", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$e", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$E", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$f", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));
        s_lexemes.insert("$F", new Lexeme(TokenType.NumberLiteral, Lexer::hexIntegerLiteral));

        s_lexemes.insert("'", new Lexeme(TokenType.CharacterLiteral, Lexer::characterLiteral));
    }

    private void addError(Result r, String code, String message) {
        var indicator = String.format("^ %s", message);
        String buffer = _buffer +
                "\n" +
                String.format("%" + (_index + 6 + indicator.length()) + "s", indicator);
        r.addError(code, buffer);
    }

    private static class Lexeme {
        public Lexeme(TokenType type) {
            this.type = type;
        }
        public Lexeme(TokenType type, boolean keyword) {
            this.type = type;
            this.keyword = keyword;
        }
        public Lexeme(TokenType type, Tokenizer tokenizer) {
            this.type = type;
            this.tokenizer = tokenizer;
        }
        public TokenType type;
        public boolean keyword;
        public Tokenizer tokenizer;
    }

    public Lexer() {
    }

    public int pos() {
        return _index;
    }

    public Token makeToken(
            TokenType type,
            int startPos,
            int endPos) {
        return new Token(type, _buffer, startPos, endPos);
    }

    public Token makeToken(
            TokenType type,
            int radix,
            int startPos,
            int endPos) {
        return new Token(type, _buffer, startPos, endPos, radix);
    }

    public Token makeToken(
            TokenType type,
            int radix,
            boolean fractional,
            int startPos,
            int endPos) {
        return new Token(type, _buffer, startPos, endPos, radix, fractional);
    }

    public String getBuffer() {
        return _buffer;
    }

    public char currentChar() {
        return _buffer.charAt(_index);
    }

    public boolean moveNext() {
        ++_index;
        return _index < _buffer.length();
    }

    public boolean tokenize(Result r, String source, ArrayList<Token> tokens) {
        _index = 0;
        _buffer = source;

        var marks = new Stack<Integer>();

        while (_index < _buffer.length()) {
            Lexeme matchedLexeme = null;
            TrieMap<Lexeme>.Node currentNode = null;

            var c = currentChar();

            if (Character.isWhitespace(c)) {
                if (!moveNext())
                    break;
                continue;
            }

            marks.push(_index);

            while (true) {
                currentNode = s_lexemes.find(currentNode, c);
                if (currentNode == null) {
                    if (matchedLexeme != null
                    &&  matchedLexeme.keyword) {
                        if (Character.isAlphabetic(c) || c == '_')
                            matchedLexeme = null;
                    }
                    break;
                }

                if (currentNode.data != null)
                    matchedLexeme = currentNode.data;

                if (!moveNext())
                    break;

                c = currentChar();
            }

            if (matchedLexeme == null) {
                addError(r, "L001", "expected valid token");
                return false;
            } else {
                if (matchedLexeme.tokenizer != null) {
                    _index = marks.pop();
                    if (!matchedLexeme.tokenizer.tokenize(r, this, tokens))
                        return false;
                } else {
                    var startPos = marks.pop();
                    var endPos = _index;
                    tokens.add(makeToken(matchedLexeme.type, startPos, endPos));
                }
            }
        }

        tokens.add(makeToken(TokenType.EndOfInput, _index, _index));
        return r.isSuccess();
    }

    public static boolean characterLiteral(Result r, Lexer lexer, ArrayList<Token> tokens) {
        var c = lexer.currentChar();
        if (c != '\'') {
            lexer.addError(r, "L002", "expected opening ' for character literal");
            return false;
        }
        if (!lexer.moveNext()) {
            lexer.addError(r,"L003", "unexpected end of input");
            return false;
        }
        var startPos = lexer.pos();
        if (!lexer.moveNext()) {
            lexer.addError(r,"L003", "unexpected end of input");
            return false;
        }
        var endPos = lexer.pos();
        c = lexer.currentChar();
        if (c != '\'') {
            lexer.addError(r,"L002", "expected closing ' for character literal");
            return false;
        }
        lexer.moveNext();

        tokens.add(lexer.makeToken(TokenType.CharacterLiteral, startPos, endPos));
        return true;
    }

    public static boolean identifierLiteral(Result r, Lexer lexer, ArrayList<Token> tokens) {
        var startPos = lexer.pos();
        var c = lexer.currentChar();
        while (Character.isAlphabetic(c) || c == '_') {
            if (!lexer.moveNext())
                break;
            c = lexer.currentChar();
        }
        tokens.add(lexer.makeToken(TokenType.Identifier, startPos, lexer.pos()));
        return true;
    }

    public static boolean hexIntegerLiteral(Result r, Lexer lexer, ArrayList<Token> tokens) {
        var c = lexer.currentChar();
        if (c == '$') {
            if (!lexer.moveNext()) {
                lexer.addError(r, "L003", "unexpected end of input");
                return false;
            }
        }
        var startPos = lexer.pos();
        c = lexer.currentChar();
        while (c == '0'
            || c == '1'
            || c == '2'
            || c == '3'
            || c == '4'
            || c == '5'
            || c == '6'
            || c == '7'
            || c == '8'
            || c == '9'
            || c == 'a'
            || c == 'A'
            || c == 'b'
            || c == 'B'
            || c == 'c'
            || c == 'C'
            || c == 'd'
            || c == 'D'
            || c == 'e'
            || c == 'E'
            || c == 'f'
            || c == 'F'
            || c == '_') {
            if (!lexer.moveNext())
                break;
            c = lexer.currentChar();
        }
        tokens.add(lexer.makeToken(TokenType.NumberLiteral, 16, startPos, lexer.pos()));
        return true;
    }

    public static boolean octalIntegerLiteral(Result r, Lexer lexer, ArrayList<Token> tokens) {
        var c = lexer.currentChar();
        if (c == '@') {
            if (!lexer.moveNext()) {
                lexer.addError(r, "L003", "unexpected end of input");
                return false;
            }
        }
        var startPos = lexer.pos();
        c = lexer.currentChar();
        while (c == '0'
            || c == '1'
            || c == '2'
            || c == '3'
            || c == '4'
            || c == '5'
            || c == '6'
            || c == '7'
            || c == '_') {
            if (!lexer.moveNext())
                break;
            c = lexer.currentChar();
        }
        tokens.add(lexer.makeToken(TokenType.NumberLiteral, 8, startPos, lexer.pos()));
        return true;
    }

    public static boolean binaryIntegerLiteral(Result r, Lexer lexer, ArrayList<Token> tokens) {
        var c = lexer.currentChar();
        if (c == '%') {
            if (!lexer.moveNext()) {
                lexer.addError(r, "L003", "unexpected end of input");
                return false;
            }
        }
        var startPos = lexer.pos();
        c = lexer.currentChar();
        while (c == '0' || c == '1' || c == '_') {
            if (!lexer.moveNext())
                break;
            c = lexer.currentChar();
        }
        tokens.add(lexer.makeToken(TokenType.NumberLiteral, 2, startPos, lexer.pos()));
        return true;
    }

    public static boolean decimalIntegerLiteral(Result r, Lexer lexer, ArrayList<Token> tokens) {
        var startPos = lexer.pos();
        var c = lexer.currentChar();
        var hasFractional = false;
        while (Character.isDigit(c) || c == '_') {
            if (!lexer.moveNext())
                break;
            c = lexer.currentChar();
            if (c == '.') {
                if (hasFractional) {
                    lexer.addError(r, "L004", "unexpected decimal point");
                    return false;
                } else {
                    hasFractional = true;
                    if (!lexer.moveNext()) {
                        lexer.addError(r, "L003", "unexpected end of input");
                        return false;
                    }
                    c = lexer.currentChar();
                }
            }
        }
        tokens.add(lexer.makeToken(
                TokenType.NumberLiteral,
                10,
                hasFractional,
                startPos,
                lexer.pos()));
        return true;
    }
}
