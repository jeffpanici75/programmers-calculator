package io.nybbles.progclac.compiler.parser;

import io.nybbles.progclac.compiler.lexer.TokenType;

public class ProductionRule {
    private Nud _nud;
    private Led _led;
    private int _lbp;
    private TokenType _tokenType;

    public ProductionRule(TokenType tokenType) {
        _tokenType = tokenType;
    }

    public Nud getNud() {
        return _nud;
    }

    public void setNud(Nud nud) {
        _nud = nud;
    }

    public Led getLed() {
        return _led;
    }

    public void setLed(Led led) {
        _led = led;
    }

    public int getLbp() {
        return _lbp;
    }

    public void setLbp(int lbp) {
        _lbp = lbp;
    }

    public TokenType getTokenType() {
        return _tokenType;
    }
}
