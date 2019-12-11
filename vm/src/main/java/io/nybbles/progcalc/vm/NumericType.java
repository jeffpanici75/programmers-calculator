package io.nybbles.progcalc.vm;

public enum NumericType {
    Float,
    Integer;

    public RegisterType getRegisterType() {
        return switch(this) {
            case Float -> RegisterType.F64;
            case Integer -> RegisterType.U64;
        };
    }
}
