package io.nybbles.progclac.compiler.ast;

import io.nybbles.progclac.compiler.lexer.TokenType;

public enum BinaryOperatorType {
    Add,
    Sub,
    Mul,
    Div,
    Mod,
    Shl,
    Shr,
    Rol,
    Ror,
    Pow,
    And,
    Or,
    Xor,
    Equals,
    LessThan,
    NotEquals,
    LogicalOr,
    LogicalAnd,
    GreaterThan,
    LessThanEquals,
    GreaterThanEquals;

    public static BinaryOperatorType fromTokenType(TokenType tokenType) {
        return switch (tokenType) {
            case Xor -> BinaryOperatorType.Xor;
            case Shl -> BinaryOperatorType.Shl;
            case Shr -> BinaryOperatorType.Shr;
            case Rol -> BinaryOperatorType.Rol;
            case Ror -> BinaryOperatorType.Ror;
            case Pipe -> BinaryOperatorType.Or;
            case Plus -> BinaryOperatorType.Add;
            case Minus -> BinaryOperatorType.Sub;
            case Caret -> BinaryOperatorType.Pow;
            case Percent -> BinaryOperatorType.Mod;
            case Asterisk -> BinaryOperatorType.Mul;
            case Equals -> BinaryOperatorType.Equals;
            case Ampersand -> BinaryOperatorType.And;
            case ForwardSlash -> BinaryOperatorType.Div;
            case LessThan -> BinaryOperatorType.LessThan;
            case NotEquals -> BinaryOperatorType.NotEquals;
            case LogicalOr -> BinaryOperatorType.LogicalOr;
            case LogicalAnd -> BinaryOperatorType.LogicalAnd;
            case GreaterThan -> BinaryOperatorType.GreaterThan;
            case LessThanEquals -> BinaryOperatorType.LessThanEquals;
            case GreaterThanEquals -> BinaryOperatorType.GreaterThanEquals;
            default -> null;
        };
    }
}
