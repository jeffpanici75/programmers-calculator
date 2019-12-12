package io.nybbles.progcalc.vm;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ByteCodeReader {
    private int _endAddress;
    private int _startAddress;
    private ByteBuffer _buffer;
    private static OperationCode[] s_opCodes;
    private static RegisterName[] s_registerNames;

    static {
        s_opCodes = OperationCode.values();
        s_registerNames = RegisterName.values();
    }

    // XXX: review register operand encoding to prevent extra padding step
    private Instruction readInstruction() {
        var operandList = new ArrayList<Operand>();
        var opCode = s_opCodes[_buffer.get()];
        for (int i = 0; i < 4; i++) {
            var operand = s_registerNames[_buffer.get()];
            if (operand != RegisterName.NN)
                operandList.add(new RegisterOperand(operand));
        }

        var flags = new byte[3];
        _buffer.get(flags, 0, 3);

        switch (flags[0]) {
            case 0x01 -> operandList.add(new ImmediateOperand(new IntegerNumeric(_buffer.getLong())));
            case 0x02 -> operandList.add(new ImmediateOperand(new FloatNumeric(_buffer.getDouble())));
            default -> _buffer.getLong();
        }

        var paddingSize = 4 - operandList.size();
        for (int i = 0; i < paddingSize; i++)
            operandList.add(new RegisterOperand(RegisterName.NN));

        var operands = new Operand[operandList.size()];
        operandList.toArray(operands);
        return new Instruction(_startAddress, opCode, operands);
    }

    public ByteCodeReader(ByteBuffer buffer, long startAddress, long endAddress) {
        _buffer = buffer;
        _endAddress = Long.valueOf(endAddress).intValue();
        _startAddress = Long.valueOf(startAddress).intValue();
    }

    public Program read() {
        var instructions = new Instruction[(_endAddress - _startAddress) / Constants.Instruction.ENCODED_SIZE];
        var i = 0;
        _buffer.position(_startAddress);
        while (_startAddress < _endAddress) {
            instructions[i++] = readInstruction();
            _startAddress += Constants.Instruction.ENCODED_SIZE;
        }
        return new Program(instructions);
    }
}
