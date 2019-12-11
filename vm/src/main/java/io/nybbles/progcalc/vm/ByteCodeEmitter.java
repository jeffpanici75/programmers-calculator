package io.nybbles.progcalc.vm;

import io.nybbles.progcalc.common.Result;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

//
// 0        1   2   3   4   5...7 8...16
// OpCode   Op1 Op2 Op3 Op4 Flags Immediate
//

public class ByteCodeEmitter {
    private int _address;
    private ByteBuffer _buffer;
    private ArrayList<EmitRecord> _records = new ArrayList<>();
    private HashMap<String, Integer> _labels = new HashMap<>();

    private enum EmitRecordType {
        Data,
        Instruction
    }

    private static class EmitRecord {
        public int address;
        public OperationCode opCode;
        public String immediateLabel;
        public Long integerImmediateOperand;
        public Double doubleImmediateOperand;
        public EmitRecordType type = EmitRecordType.Instruction;
        public ArrayList<RegisterName> operands = new ArrayList<>();
    }

    private static int align(int value, int align) {
        if (align > 0) {
            var result = value + align - 1;
            return result - result % align;
        }
        return value;
    }

    private void addEmitRecord(long value) {
        var record = new EmitRecord();
        record.address = align(_address, Long.BYTES);
        record.type = EmitRecordType.Data;
        record.integerImmediateOperand = value;
        _records.add(record);
        _address += Long.BYTES;
    }

    private void addEmitRecord(String label) {
        var record = new EmitRecord();
        record.address = align(_address, Long.BYTES);
        record.immediateLabel = label;
        record.type = EmitRecordType.Data;
        _records.add(record);
        _address += Long.BYTES;
    }

    private void addEmitRecord(double value) {
        var record = new EmitRecord();
        record.address = align(_address, Double.BYTES);
        record.type = EmitRecordType.Data;
        record.doubleImmediateOperand = value;
        _records.add(record);
        _address += Double.BYTES;
    }

    private void addEmitRecord(
            OperationCode opCode,
            RegisterName op1,
            RegisterName op2,
            RegisterName op3,
            RegisterName op4) {
        var record = new EmitRecord();
        record.address = align(_address, 16);
        record.opCode = opCode;
        record.operands.add(op1);
        record.operands.add(op2);
        record.operands.add(op3);
        record.operands.add(op4);
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, String label) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.immediateLabel = label;
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, long immediate) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.integerImmediateOperand = immediate;
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, double immediate) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.doubleImmediateOperand = immediate;
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, RegisterName op1) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.operands.add(op1);
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, RegisterName op1, String label) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.operands.add(op1);
        record.immediateLabel = label;
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, RegisterName op1, long immediate) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.operands.add(op1);
        record.integerImmediateOperand = immediate;
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, RegisterName op1, double immediate) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.operands.add(op1);
        record.doubleImmediateOperand = immediate;
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, RegisterName op1, RegisterName op2) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.operands.add(op1);
        record.operands.add(op2);
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, RegisterName op1, RegisterName op2, RegisterName op3) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.operands.add(op1);
        record.operands.add(op2);
        record.operands.add(op3);
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, RegisterName op1, RegisterName op2, long immediate) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.operands.add(op1);
        record.operands.add(op2);
        record.integerImmediateOperand = immediate;
        _records.add(record);
        _address += 16;
    }

    private void addEmitRecord(OperationCode opCode, RegisterName op1, RegisterName op2, double immediate) {
        var record = new EmitRecord();
        record.opCode = opCode;
        record.address = align(_address, 16);
        record.operands.add(op1);
        record.operands.add(op2);
        record.doubleImmediateOperand = immediate;
        _records.add(record);
        _address += 16;
    }

    public ByteCodeEmitter(ByteBuffer buffer, long address) {
        _buffer = buffer;
        _address = Long.valueOf(address).intValue();
    }

    public long getAddress() {
        return _address;
    }

    public String[] getLabels() {
        String[] names = new String[_labels.size()];
        return _labels.keySet().toArray(names);
    }

    public boolean emit(Result r) {
        for (var record : _records) {
            if (record.immediateLabel != null) {
                if (!_labels.containsKey(record.immediateLabel)) {
                    r.addError(
                            "E001",
                            String.format("Undefined label: %s", record.immediateLabel));
                    continue;
                }
                record.integerImmediateOperand = _labels.get(record.immediateLabel).longValue();
            }
        }

        if (!r.isSuccess())
            return false;

        for (var record : _records) {
            _buffer.position(record.address);
            switch (record.type) {
                case Data -> {
                    if (record.integerImmediateOperand != null) {
                        _buffer.putLong(record.integerImmediateOperand);
                    } else if (record.doubleImmediateOperand != null) {
                        _buffer.putDouble(record.doubleImmediateOperand);
                    }
                }
                case Instruction -> {
                    _buffer.put((byte)record.opCode.ordinal());
                    for (int i = 0; i < 4; i++) {
                        if (i < record.operands.size())
                            _buffer.put((byte)record.operands.get(i).ordinal());
                        else
                            _buffer.put((byte)RegisterName.NN.ordinal());
                    }
                    if (record.integerImmediateOperand != null) {
                        _buffer.put((byte) 0x01);
                        _buffer.put((byte) 0);
                        _buffer.put((byte) 0);
                        _buffer.putLong(record.integerImmediateOperand);
                    } else if (record.doubleImmediateOperand != null) {
                        _buffer.put((byte) 0x02);
                        _buffer.put((byte) 0);
                        _buffer.put((byte) 0);
                        _buffer.putDouble(record.doubleImmediateOperand);
                    } else {
                        _buffer.put((byte) 0xff);
                        _buffer.put((byte) 0);
                        _buffer.put((byte) 0);
                        _buffer.putLong(0L);
                    }
                }
            }
        }

        return true;
    }

    public void setAddress(long value) {
        _address = Long.valueOf(value).intValue();
    }

    public ByteCodeEmitter dq(long value) {
        addEmitRecord(value);
        return this;
    }

    public ByteCodeEmitter align(int size) {
        _address = align(_address, size);
        return this;
    }

    public ByteCodeEmitter dq(double value) {
        addEmitRecord(value);
        return this;
    }

    public ByteCodeEmitter dq(String label) {
        addEmitRecord(label);
        return this;
    }

    public long getLabelAddress(String label) {
        return _labels.get(label);
    }

    public ByteCodeEmitter label(String name) {
        if (!_labels.containsKey(name)) {
            _address = align(_address, 16);
            _labels.put(name, _address);
        }
        return this;
    }

    public ByteCodeEmitter nop() {
        addEmitRecord(OperationCode.NOP);
        return this;
    }

    public ByteCodeEmitter exit() {
        addEmitRecord(OperationCode.EXIT);
        return this;
    }

    public ByteCodeEmitter inc(RegisterName target) {
        addEmitRecord(OperationCode.INC, target);
        return this;
    }

    public ByteCodeEmitter dec(RegisterName target) {
        addEmitRecord(OperationCode.DEC, target);
        return this;
    }

    public ByteCodeEmitter jmp(long addr) {
        addEmitRecord(OperationCode.JMP, addr);
        return this;
    }

    public ByteCodeEmitter push(long value) {
        addEmitRecord(OperationCode.PUSH, value);
        return this;
    }

    public ByteCodeEmitter push(double value) {
        addEmitRecord(OperationCode.PUSH, value);
        return this;
    }

    public ByteCodeEmitter jmp(RegisterName target) {
        addEmitRecord(OperationCode.JMP, target);
        return this;
    }

    public ByteCodeEmitter jmp(String label) {
        addEmitRecord(OperationCode.JMP, label);
        return this;
    }

    public ByteCodeEmitter pop(RegisterName target) {
        addEmitRecord(OperationCode.POP, target);
        return this;
    }

    public ByteCodeEmitter clr(RegisterName target) {
        addEmitRecord(OperationCode.CLR, target);
        return this;
    }

    public ByteCodeEmitter push(RegisterName target) {
        addEmitRecord(OperationCode.PUSH, target);
        return this;
    }

    public ByteCodeEmitter trap(long value) {
        addEmitRecord(OperationCode.TRAP, value);
        return this;
    }

    public ByteCodeEmitter move(RegisterName target, long value) {
        addEmitRecord(OperationCode.MOVE, target, value);
        return this;
    }

    public ByteCodeEmitter move(RegisterName target, String label) {
        addEmitRecord(OperationCode.MOVE, target, label);
        return this;
    }

    public ByteCodeEmitter move(RegisterName target, double value) {
        addEmitRecord(OperationCode.MOVE, target, value);
        return this;
    }

    public ByteCodeEmitter move(RegisterName target, RegisterName value) {
        addEmitRecord(OperationCode.MOVE, target, value);
        return this;
    }

    public ByteCodeEmitter beq(RegisterName value, String label) {
        addEmitRecord(OperationCode.BEQ, value, label);
        return this;
    }

    public ByteCodeEmitter beq(RegisterName value, long addr) {
        addEmitRecord(OperationCode.BEQ, value, addr);
        return this;
    }

    public ByteCodeEmitter beq(RegisterName value, RegisterName addr) {
        addEmitRecord(OperationCode.BEQ, value, addr);
        return this;
    }

    public ByteCodeEmitter bne(RegisterName value, String label) {
        addEmitRecord(OperationCode.BNE, value, label);
        return this;
    }

    public ByteCodeEmitter bne(RegisterName value, long addr) {
        addEmitRecord(OperationCode.BNE, value, addr);
        return this;
    }

    public ByteCodeEmitter bne(RegisterName value, RegisterName addr) {
        addEmitRecord(OperationCode.BNE, value, addr);
        return this;
    }

    public ByteCodeEmitter bg(RegisterName value, String label) {
        addEmitRecord(OperationCode.BG, value, label);
        return this;
    }

    public ByteCodeEmitter bg(RegisterName value, long addr) {
        addEmitRecord(OperationCode.BG, value, addr);
        return this;
    }

    public ByteCodeEmitter bg(RegisterName value, RegisterName addr) {
        addEmitRecord(OperationCode.BG, value, addr);
        return this;
    }

    public ByteCodeEmitter bl(RegisterName value, String label) {
        addEmitRecord(OperationCode.BL, value, label);
        return this;
    }

    public ByteCodeEmitter bl(RegisterName value, long addr) {
        addEmitRecord(OperationCode.BL, value, addr);
        return this;
    }

    public ByteCodeEmitter bl(RegisterName value, RegisterName addr) {
        addEmitRecord(OperationCode.BL, value, addr);
        return this;
    }

    public ByteCodeEmitter ble(RegisterName value, String label) {
        addEmitRecord(OperationCode.BLE, value, label);
        return this;
    }

    public ByteCodeEmitter ble(RegisterName value, long addr) {
        addEmitRecord(OperationCode.BLE, value, addr);
        return this;
    }

    public ByteCodeEmitter ble(RegisterName value, RegisterName addr) {
        addEmitRecord(OperationCode.BLE, value, addr);
        return this;
    }

    public ByteCodeEmitter bge(RegisterName value, String label) {
        addEmitRecord(OperationCode.BGE, value, label);
        return this;
    }

    public ByteCodeEmitter bge(RegisterName value, long addr) {
        addEmitRecord(OperationCode.BGE, value, addr);
        return this;
    }

    public ByteCodeEmitter bge(RegisterName value, RegisterName addr) {
        addEmitRecord(OperationCode.BGE, value, addr);
        return this;
    }

    public ByteCodeEmitter sete(RegisterName target, RegisterName value) {
        addEmitRecord(OperationCode.SETE, target, value);
        return this;
    }

    public ByteCodeEmitter setne(RegisterName target, RegisterName value) {
        addEmitRecord(OperationCode.SETNE, target, value);
        return this;
    }

    public ByteCodeEmitter setg(RegisterName target, RegisterName value) {
        addEmitRecord(OperationCode.SETG, target, value);
        return this;
    }

    public ByteCodeEmitter setl(RegisterName target, RegisterName value) {
        addEmitRecord(OperationCode.SETL, target, value);
        return this;
    }

    public ByteCodeEmitter setge(RegisterName target, RegisterName value) {
        addEmitRecord(OperationCode.SETGE, target, value);
        return this;
    }

    public ByteCodeEmitter setle(RegisterName target, RegisterName value) {
        addEmitRecord(OperationCode.SETLE, target, value);
        return this;
    }

    public ByteCodeEmitter load(RegisterName addr, RegisterName value) {
        addEmitRecord(OperationCode.LOAD, addr, value);
        return this;
    }

    public ByteCodeEmitter store(RegisterName addr, RegisterName value) {
        addEmitRecord(OperationCode.STORE, addr, value);
        return this;
    }

    public ByteCodeEmitter cmp(RegisterName result, RegisterName lhs, RegisterName rhs) {
        addEmitRecord(OperationCode.CMP, result, lhs, rhs);
        return this;
    }

    public ByteCodeEmitter add(RegisterName sum, RegisterName augend, RegisterName addend) {
        addEmitRecord(OperationCode.ADD, sum, augend, addend);
        return this;
    }

    public ByteCodeEmitter sub(RegisterName difference, RegisterName minuend, RegisterName subtrahend) {
        addEmitRecord(OperationCode.SUB, difference, minuend, subtrahend);
        return this;
    }

    public ByteCodeEmitter mul(RegisterName product, RegisterName multiplicand, RegisterName multiplier) {
        addEmitRecord(OperationCode.MUL, product, multiplicand, multiplier);
        return this;
    }

    public ByteCodeEmitter div(RegisterName quotient, RegisterName dividend, RegisterName divisor) {
        addEmitRecord(OperationCode.DIV, quotient, dividend, divisor);
        return this;
    }

    public ByteCodeEmitter mod(RegisterName remainder, RegisterName dividend, RegisterName divisor) {
        addEmitRecord(OperationCode.MOD, remainder, dividend, divisor);
        return this;
    }

    public ByteCodeEmitter pow(RegisterName power, RegisterName base, RegisterName exponent) {
        addEmitRecord(OperationCode.POW, power, base, exponent);
        return this;
    }

    public ByteCodeEmitter neg(RegisterName negated, RegisterName base) {
        addEmitRecord(OperationCode.NEG, negated, base);
        return this;
    }

    public ByteCodeEmitter not(RegisterName inverted, RegisterName base) {
        addEmitRecord(OperationCode.NOT, inverted, base);
        return this;
    }

    public ByteCodeEmitter and(RegisterName target, RegisterName lhs, RegisterName rhs) {
        addEmitRecord(OperationCode.AND, target, lhs, rhs);
        return this;
    }

    public ByteCodeEmitter or(RegisterName target, RegisterName lhs, RegisterName rhs) {
        addEmitRecord(OperationCode.OR, target, lhs, rhs);
        return this;
    }

    public ByteCodeEmitter xor(RegisterName target, RegisterName lhs, RegisterName rhs) {
        addEmitRecord(OperationCode.XOR, target, lhs, rhs);
        return this;
    }

    public ByteCodeEmitter shr(RegisterName target, RegisterName lhs, RegisterName rhs) {
        addEmitRecord(OperationCode.SHR, target, lhs, rhs);
        return this;
    }

    public ByteCodeEmitter shl(RegisterName target, RegisterName lhs, RegisterName rhs) {
        addEmitRecord(OperationCode.SHL, target, lhs, rhs);
        return this;
    }

    public ByteCodeEmitter rol(RegisterName target, RegisterName lhs, RegisterName rhs) {
        addEmitRecord(OperationCode.ROL, target, lhs, rhs);
        return this;
    }

    public ByteCodeEmitter ror(RegisterName target, RegisterName lhs, RegisterName rhs) {
        addEmitRecord(OperationCode.ROR, target, lhs, rhs);
        return this;
    }

    public ByteCodeEmitter madd(RegisterName product, RegisterName multiplicand, RegisterName multiplier, RegisterName addend) {
        addEmitRecord(OperationCode.MADD, product, multiplicand, multiplier, addend);
        return this;
    }

}
