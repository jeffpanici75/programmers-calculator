package io.nybbles.progcalc.vm;

public class Register {
    private Numeric _value;
    private RegisterName _name;

    private Register(RegisterName name, Numeric value) {
        _name = name;
        _value = value;
    }

    public static Register fromName(RegisterName name) {
        var type = name.getType();
        return switch (type) {
            case U0, U64  -> new Register(name, new IntegerNumeric(0));
            case F64      -> new Register(name, new FloatNumeric(0.0));
        };
    }

    public Numeric getValue() {
        return _value;
    }

    public RegisterName getName() {
        return _name;
    }

    public RegisterType getType() {
        return _name.getType();
    }
}
