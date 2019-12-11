package io.nybbles.progcalc.vm;

import java.util.ArrayList;
import java.util.HashMap;

public class Program {
    private int _index;
    private Instruction[] _instructions;
    private RegisterName _targetRegister;
    private HashMap<Long, Integer> _addressMap = new HashMap<>();

    public Program(Instruction[] instructions) {
        _instructions = instructions;
        var i = 0;
        for (var instruction : _instructions) {
            _addressMap.put(instruction.getAddress(), i++);
        }
    }

    public void end() {
        _index = _instructions.length;
    }

    public void reset() {
        _index = 0;
    }

    public ArrayList<String> disassemble() {
        var lines = new ArrayList<String>();
        for (var instruction : _instructions) {
            var buffer = new StringBuilder();
            buffer.append(String.format(
                    "$%08X: %-8s ",
                    instruction.getAddress(),
                    instruction.getOpCode()));
            var operands = new ArrayList<Operand>();
            for (var operand : instruction.getOperands()) {
                switch (operand.getType()) {
                    case Register -> {
                        var r0 = operand.getRegisterName();
                        if (r0 != RegisterName.NN)
                            operands.add(operand);
                    }
                    case Immediate -> operands.add(operand);
                }
            }
            for (int i = 0; i < operands.size(); i++) {
                if (i > 0)
                    buffer.append(", ");
                var operand = operands.get(i);
                switch (operand.getType()) {
                    case Register -> buffer.append(String.format("%s", operand.getRegisterName()));
                    case Immediate -> {
                        var immediate = operand.getImmediateOperand();
                        switch (immediate.getType()) {
                            case Integer -> buffer.append(String.format("#$%016X", immediate.asInteger()));
                            case Float   -> buffer.append(String.format("#$%016X", Double.doubleToRawLongBits(immediate.asFloat())));
                        }
                    }
                }
            }
            lines.add(buffer.toString());
        }
        return lines;
    }

    public int instructionCount() {
        return _instructions.length;
    }

    public long getStartAddress() {
        if (_instructions.length == 0) return 0;
        return _instructions[0].getAddress();
    }

    public boolean hasInstructions() {
        return _instructions.length > 0;
    }

    public Instruction nextInstruction() {
        if (_index < _instructions.length)
            return _instructions[_index++];
        return null;
    }

    public RegisterName getTargetRegister() {
        return _targetRegister;
    }

    public boolean nextInstructionByAddress(long address) {
        if (!_addressMap.containsKey(address))
            return false;
        _index = _addressMap.get(address);
        return true;
    }

    public void setTargetRegister(RegisterName targetRegister) {
        _targetRegister = targetRegister;
    }
}
