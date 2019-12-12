package io.nybbles.progcalc.vm;

import io.nybbles.progcalc.common.Result;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class VirtualMachine {
    private Register _pc;
    private Register _sp;
    private int _dataSize;
    private int _heapSize;
    private int _stackSize;
    private ByteBuffer _heap;
    private Program _program;
    private Register[] _registers;
    private HashMap<Long, Trap> _traps = new HashMap<>();

    private boolean performBranch(Result r, Operand op) {
        switch (op.getType()) {
            case Register -> {
                var op1r = (RegisterOperand) op;
                var r1 = getRegister(op1r.getRegisterName());
                if (!_program.nextInstructionByAddress(r1.getValue().asInteger())) {
                    r.addError(
                            "V021",
                            String.format("Invalid branch address: %d", r1.getValue().asInteger()));
                    return false;
                }
            }
            case Immediate -> {
                var op1i = (ImmediateOperand) op;
                if (!_program.nextInstructionByAddress(op1i.getImmediateOperand().asInteger())) {
                    r.addError(
                            "V021",
                            String.format("Invalid branch address: %d", op1i.getImmediateOperand().asInteger()));
                    return false;
                }
            }
        }
        return true;
    }

    public VirtualMachine(int heapSize, int stackSize, int dataSize) {
        _heapSize = heapSize;
        _dataSize = dataSize;
        _stackSize = stackSize;
    }

    public long pop() {
        var sp = _sp.getValue();
        var tos = _heap.getLong((int) sp.asInteger());
        _sp.getValue().setNumeric(sp.add(8));
        return tos;
    }

    public void clearHeap() {
        _heap.clear();
    }

    public int getDataSize() {
        return _dataSize;
    }

    public int getHeapSize() {
        return _heapSize;
    }

    public int getStackSize() {
        return _stackSize;
    }

    public int getStackDepth() {
        return (int) (_heapSize - _sp.getValue().asInteger()) / Long.BYTES;
    }

    public long topOfStack() {
        var sp = _sp.getValue();
        return _heap.getLong((int) sp.asInteger());
    }

    public double popDouble() {
        var sp = _sp.getValue();
        var tos = _heap.getDouble((int) sp.asInteger());
        _sp.getValue().setNumeric(sp.add(8));
        return tos;
    }

    public int getStackBottom() {
        return _heapSize - _stackSize;
    }

    public boolean isStackEmpty() {
        var sp = _sp.getValue();
        return sp.asInteger() == _heapSize;
    }

    public boolean run(Result r) {
        while (true) {
            var success = step(r);
            if (!success)
                break;
        }
        return r.isSuccess();
    }

    public void push(long value) {
        var sp = _sp.getValue();
        sp = sp.sub(8);
        _heap.putLong((int) sp.asInteger(), value);
        _sp.getValue().setNumeric(sp);
    }

    public boolean step(Result r) {
        var instruction = _program.nextInstruction();
        if (instruction == null)
            return false;

        _pc.getValue().setInteger(instruction.getAddress());
        var operands = instruction.getOperands();
        var op0 = operands[0];
        var op1 = operands[1];
        var op2 = operands[2];
        var op3 = operands[3];

        switch (instruction.getOpCode()) {
            case NOP -> {}
            case INC -> {
                var r0 = getRegister(op0.getRegisterName());
                var value = r0.getValue();
                r0.getValue().setNumeric(value.add(1));
            }
            case DEC -> {
                var r0 = getRegister(op0.getRegisterName());
                var value = r0.getValue();
                r0.getValue().setNumeric(value.sub(1));
            }
            case ADD -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().add(r2.getValue()));
            }
            case SUB -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().sub(r2.getValue()));
            }
            case MUL -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().mul(r2.getValue()));
            }
            case MADD -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                var r3 = getRegister(op3.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().mul(r2.getValue()).add(r3.getValue()));
            }
            case DIV -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().div(r2.getValue()));
            }
            case MOD -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().mod(r2.getValue()));
            }
            case POW -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().pow(r2.getValue()));
            }
            case NEG -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().neg());
            }
            case NOT -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().not());
            }
            case AND -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().and(r2.getValue()));
            }
            case OR  -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().or(r2.getValue()));
            }
            case XOR -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().xor(r2.getValue()));
            }
            case SHR -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().shr(r2.getValue()));
            }
            case SHL -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().shl(r2.getValue()));
            }
            case ROL -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().rol(r2.getValue()));
            }
            case ROR -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                r0.getValue().setNumeric(r1.getValue().ror(r2.getValue()));
            }
            case CLR -> {
                var r0 = getRegister(op0.getRegisterName());
                r0.getValue().setInteger(0L);
            }
            case PUSH -> {
                switch (instruction.getRegisterMode()) {
                    case U64, F64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        push(r0.getValue());
                    }
                    case U0 -> push(op0.getImmediateOperand());
                }
            }
            case POP -> {
                var r0 = getRegister(op0.getRegisterName());

                switch (instruction.getRegisterMode()) {
                    case U64 -> r0.getValue().setInteger(pop());
                    case F64 -> r0.getValue().setFloat(popDouble());
                }
            }
            case TRAP -> {
                var value = op0.getImmediateOperand().asInteger();

                if (!_traps.containsKey(value)) {
                    r.addError(
                            "V033",
                            String.format("Undefined trap vector: %d", value));
                    return false;
                }

                var trap = _traps.get(value);
                if (!trap.execute(r, this))
                    return false;
            }
            case EXIT -> {
                _program.end();
                return false;
            }
            case MOVE -> {
                var r0 = getRegister(op0.getRegisterName());
                switch (op1.getType()) {
                    case Register -> {
                        var r1 = getRegister(op1.getRegisterName());
                        r0.getValue().setNumeric(r1.getValue());
                    }
                    case Immediate -> r0.getValue().setNumeric(op1.getImmediateOperand());
                }
            }
            case LOAD  -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());

                switch (r1.getType()) {
                    case U64 -> r1.getValue().setInteger(_heap.getLong((int) r0.getValue().asInteger()));
                    case F64 -> r1.getValue().setFloat(_heap.getDouble((int) r0.getValue().asInteger()));
                }
            }
            case STORE -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());

                switch (r1.getType()) {
                    case U64 -> _heap.putLong((int) r0.getValue().asInteger(), r1.getValue().asInteger());
                    case F64 -> _heap.putDouble((int) r0.getValue().asInteger(), r1.getValue().asFloat());
                }
            }
            case CMP   -> {
                var r0 = getRegister(op0.getRegisterName());
                var r1 = getRegister(op1.getRegisterName());
                var r2 = getRegister(op2.getRegisterName());
                var temp = r1.getValue().sub(r2.getValue());
                if (temp.getType() == NumericType.Float) {
                    temp.setFloat(Math.ceil(temp.asFloat()));
                }
                var diff = temp.asInteger();
                if (diff == 0) {
                    r0.getValue().setInteger(0L);
                } else if (diff < 0) {
                    r0.getValue().setInteger(-1L);
                } else {
                    r0.getValue().setInteger(1L);
                }
            }
            case SETE  -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        var r1 = getRegister(op1.getRegisterName());
                        r0.getValue().setInteger(r1.getValue().asInteger() == 0 ? 1L : 0L);
                    }
                    case F64 -> {
                        r.addError("V003", "SETE does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case SETNE -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        var r1 = getRegister(op1.getRegisterName());
                        r0.getValue().setInteger(r1.getValue().asInteger() != 0 ? 1L : 0L);
                    }
                    case F64 -> {
                        r.addError("V004", "SETNE does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case SETG -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        var r1 = getRegister(op1.getRegisterName());
                        r0.getValue().setInteger(r1.getValue().asInteger() > 0 ? 1L : 0L);
                    }
                    case F64 -> {
                        r.addError("V004", "SETG does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case SETGE -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        var r1 = getRegister(op1.getRegisterName());
                        r0.getValue().setInteger(r1.getValue().asInteger() >= 0 ? 1L : 0L);
                    }
                    case F64 -> {
                        r.addError("V004", "SETGE does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case SETL -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        var r1 = getRegister(op1.getRegisterName());
                        r0.getValue().setInteger(r1.getValue().asInteger() < 0 ? 1L : 0L);
                    }
                    case F64 -> {
                        r.addError("V004", "SETL does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case SETLE -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        var r1 = getRegister(op1.getRegisterName());
                        r0.getValue().setInteger(r1.getValue().asInteger() <= 0 ? 1L : 0L);
                    }
                    case F64 -> {
                        r.addError("V004", "SETLE does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case BEQ -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        if (r0.getValue().asInteger() == 0) {
                            if (!performBranch(r, operands[1]))
                                return false;
                        }
                    }
                    case F64 -> {
                        r.addError("V016", "BEQ does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case BNE -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        if (r0.getValue().asInteger() != 0) {
                            if (!performBranch(r, operands[1]))
                                return false;
                        }
                    }
                    case F64 -> {
                        r.addError("V016", "BNE does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case BG  -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        if (r0.getValue().asInteger() > 0) {
                            if (!performBranch(r, operands[1]))
                                return false;
                        }
                    }
                    case F64 -> {
                        r.addError("V016", "BG does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case BL  -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        if (r0.getValue().asInteger() < 0) {
                            if (!performBranch(r, operands[1]))
                                return false;
                        }
                    }
                    case F64 -> {
                        r.addError("V016", "BL does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case BGE -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        if (r0.getValue().asInteger() >= 0) {
                            if (!performBranch(r, operands[1]))
                                return false;
                        }
                    }
                    case F64 -> {
                        r.addError("V016", "BGE does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case BLE -> {
                switch (instruction.getRegisterMode()) {
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        if (r0.getValue().asInteger() <= 0) {
                            if (!performBranch(r, operands[1]))
                                return false;
                        }
                    }
                    case F64 -> {
                        r.addError("V016", "BLE does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
            case JMP -> {
                switch (instruction.getRegisterMode()) {
                    case U0 -> {
                        if (!_program.nextInstructionByAddress(op0.getImmediateOperand().asInteger())) {
                            r.addError(
                                    "V021",
                                    String.format(
                                            "Invalid branch address: %d",
                                            op0.getImmediateOperand().asInteger()));
                            return false;
                        }
                    }
                    case U64 -> {
                        var r0 = getRegister(op0.getRegisterName());
                        if (!_program.nextInstructionByAddress(r0.getValue().asInteger())) {
                            r.addError(
                                    "V021",
                                    String.format(
                                            "Invalid branch address: %d",
                                            r0.getValue().asInteger()));
                            return false;
                        }
                    }
                    case F64 -> {
                        r.addError(
                                "V006",
                                "JMP does not accept float registers: F0-F15");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void push(Numeric value) {
        switch (value.getType()) {
            case Float   -> push(value.asFloat());
            case Integer -> push(value.asInteger());
        }
    }

    public void push(double value) {
        var sp = _sp.getValue();
        sp = sp.sub(8);
        _heap.putDouble((int) sp.asInteger(), value);
        _sp.getValue().setNumeric(sp);
    }

    public int getDataStartAddress() {
        return _heapSize - _stackSize - _dataSize;
    }

    public boolean initialize(Result r) {
        _heap = ByteBuffer.allocate(_heapSize);

        _registers = new Register[RegisterName.size()];
        var i = 0;
        for (var name : RegisterName.values())
            _registers[i++] = Register.fromName(name);

        _pc = getRegister(RegisterName.PC);
        _sp = getRegister(RegisterName.SP);

        _pc.getValue().setInteger(0L);
        _sp.getValue().setInteger(_heapSize);

        return true;
    }

    public long getHeapLong(long address) {
        return _heap.getLong((int)address);
    }

    public void removeTrapHandler(long id) {
        _traps.remove(id);
    }

    public void setProgram(Program program) {
        _program = program;
        _program.reset();
    }

    public double getHeapDouble(long address) {
        return _heap.getDouble((int)address);
    }

    public void addTrapHandler(long id, Trap trap) {
        _traps.put(id, trap);
    }

    public Register getRegister(RegisterName name) {
        return _registers[name.ordinal()];
    }

    public ByteCodeEmitter getEmitter(long address) {
        return new ByteCodeEmitter(_heap, address);
    }

    public void putHeapLong(long address, long value) {
        _heap.putLong((int)address, value);
    }

    public void putHeapDouble(long address, double value) {
        _heap.putDouble((int)address, value);
    }

    public ByteCodeReader getReader(long startAddress, long endAddress) {
        return new ByteCodeReader(_heap, startAddress, endAddress);
    }
}
