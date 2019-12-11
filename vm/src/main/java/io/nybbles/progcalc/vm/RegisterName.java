package io.nybbles.progcalc.vm;

public enum RegisterName {
    PC,
    SP,
    NN,
    I0,
    I1,
    I2,
    I3,
    I4,
    I5,
    I6,
    I7,
    I8,
    I9,
    I10,
    I11,
    I12,
    I13,
    I14,
    I15,
    F0,
    F1,
    F2,
    F3,
    F4,
    F5,
    F6,
    F7,
    F8,
    F9,
    F10,
    F11,
    F12,
    F13,
    F14,
    F15;

    public static int size() {
        return RegisterName.values().length;
    }

    public RegisterType getType() {
        return switch (this) {
            case NN -> RegisterType.U0;
            case PC, SP,
                    I0, I1, I2, I3, I4,
                    I5, I6, I7, I8, I9,
                    I10, I11, I12, I13, I14, I15 -> RegisterType.U64;
            case F0, F1, F2, F3, F4,
                    F5, F6, F7, F8, F9,
                    F10, F11, F12, F13, F14, F15 -> RegisterType.F64;
        };
    }

}
