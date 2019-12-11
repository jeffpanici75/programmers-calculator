package io.nybbles.progcalc.vm;

public enum RegisterType {
    U0,
    U64,
    F64;

    public NumericType getNumericType() {
        return switch (this) {
            case U0, U64 -> NumericType.Integer;
            case F64     -> NumericType.Float;
        };
    }
}
