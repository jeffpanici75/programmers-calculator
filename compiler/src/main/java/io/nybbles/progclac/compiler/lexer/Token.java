package io.nybbles.progclac.compiler.lexer;

public class Token {
    private int _end;
    private int _start;
    private String _buffer;
    private TokenType _type;
    private int _radix = 10;
    private boolean _fractional;

    public Token(
            TokenType type,
            String buffer,
            int start,
            int end) {
        _end = end;
        _type = type;
        _start = start;
        _buffer = buffer;
    }

    public Token(
            TokenType type,
            String buffer,
            int start,
            int end,
            int radix) {
        _end = end;
        _type = type;
        _radix = radix;
        _start = start;
        _buffer = buffer;
    }

    public Token(
            TokenType type,
            String buffer,
            int start,
            int end,
            int radix,
            boolean fractional) {
        _end = end;
        _type = type;
        _radix = radix;
        _start = start;
        _buffer = buffer;
        _fractional = fractional;
    }

    public int getEnd() {
        return _end;
    }

    public int getRadix() {
        return _radix;
    }

    public int getStart() {
        return _start;
    }

    public String getSlice() {
        var slice = _buffer.substring(_start, _end);
        switch (_type) {
            case NumberLiteral -> {
                var builder = new StringBuilder();
                for (int i = 0; i < slice.length(); i++) {
                    var c = slice.charAt(i);
                    if (Character.isLetterOrDigit(c) || c == '.')
                        builder.append(c);
                }
                return builder.toString();
            }
            default -> { return slice; }
        }
    }

    public TokenType getType() {
        return _type;
    }

    public boolean isFractional() {
        return _fractional;
    }
}
