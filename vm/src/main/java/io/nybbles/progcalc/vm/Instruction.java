package io.nybbles.progcalc.vm;

public class Instruction {
    private long _address;
    private OperationCode _opCode;
    private Operand[] _operands;

    public Instruction(long address, OperationCode opCode, Operand[] operands) {
        _opCode = opCode;
        _address = address;
        _operands = operands;
    }

    long getAddress() {
        return _address;
    }

    Operand[] getOperands() {
        return _operands;
    }

    OperationCode getOpCode() {
        return _opCode;
    }

    RegisterType getRegisterMode() {
        if (_operands.length == 0)
            return RegisterType.U0;
        var op0 = _operands[0];
        if (op0.getType() == OperandType.Register) {
            return op0.getRegisterName().getType();
        } else {
            return RegisterType.U0;
        }
    }
}
