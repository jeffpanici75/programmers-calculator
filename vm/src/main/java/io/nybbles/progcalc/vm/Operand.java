package io.nybbles.progcalc.vm;

public interface Operand {
    OperandType getType();

    Numeric getImmediateOperand();

    RegisterName getRegisterName();
}
