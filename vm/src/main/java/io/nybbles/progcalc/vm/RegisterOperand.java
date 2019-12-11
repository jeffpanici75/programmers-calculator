package io.nybbles.progcalc.vm;

public class RegisterOperand implements Operand {
    private RegisterName _name;

    public RegisterOperand(RegisterName name) {
        _name = name;
    }

    @Override
    public OperandType getType() {
        return OperandType.Register;
    }

    @Override
    public Numeric getImmediateOperand() {
        return null;
    }

    @Override
    public RegisterName getRegisterName() {
        return _name;
    }
}
