package io.nybbles.progcalc.vm;

public class ImmediateOperand implements Operand {
    private Numeric _value;

    public ImmediateOperand(Numeric value) {
        _value = value;
    }

    @Override
    public OperandType getType() {
        return OperandType.Immediate;
    }

    @Override
    public Numeric getImmediateOperand() {
        return _value;
    }

    @Override
    public RegisterName getRegisterName() {
        return null;
    }
}
