package io.nybbles.progclac.compiler;

import io.nybbles.progcalc.vm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class SymbolTable {
    private HashMap<String, TypedAddress> _symbolAddresses = new HashMap<>();
    private Stack<Integer> _freeAddresses = new Stack<>();
    private VirtualMachine _vm;

    private static class TypedAddress {
        public TypedAddress(NumericType type, Integer address) {
            this.type = type;
            this.address = address;
        }
        public Integer address;
        public NumericType type;
    }

    public SymbolTable(VirtualMachine vm) {
        _vm = vm;
        _freeAddresses.push(_vm.getDataStartAddress());
    }

    public boolean hasSymbol(String name) {
        return _symbolAddresses.containsKey(name);
    }

    public Numeric getSymbol(String name) {
        var typedAddress = _symbolAddresses.getOrDefault(name, null);
        if (typedAddress == null)
            return null;
        return switch (typedAddress.type) {
            case Float   -> new FloatNumeric(_vm.getHeapDouble(typedAddress.address));
            case Integer -> new IntegerNumeric(_vm.getHeapLong(typedAddress.address));
        };
    }

    public Long getSymbolAddress(String name) {
        var typedAddress = _symbolAddresses.getOrDefault(name, null);
        if (typedAddress == null)
            return null;
        return Long.valueOf(typedAddress.address);
    }

    public ArrayList<String> getSymbols() {
        return new ArrayList<String>(_symbolAddresses.keySet());
    }

    public boolean removeSymbol(String name) {
        var typedAddress = _symbolAddresses.remove(name);
        if (typedAddress != null) {
            _freeAddresses.push(typedAddress.address);
            return true;
        }
        return false;
    }

    public NumericType getSymbolType(String name) {
        var typedAddress = _symbolAddresses.getOrDefault(name, null);
        if (typedAddress == null)
            return null;
        return typedAddress.type;
    }

    public boolean setSymbol(String name, long value) {
        var typedAddress = _symbolAddresses.getOrDefault(name, null);
        if (typedAddress == null)
            return false;
        _vm.putHeapLong(typedAddress.address, value);
        return true;
    }

    public boolean setSymbol(String name, double value) {
        var typedAddress = _symbolAddresses.getOrDefault(name, null);
        if (typedAddress == null)
            return false;
        _vm.putHeapDouble(typedAddress.address, value);
        return true;
    }

    public boolean setSymbol(String name, Numeric value) {
        var typedAddress = _symbolAddresses.getOrDefault(name, null);
        if (typedAddress == null)
            return false;
        switch (value.getType()) {
            case Float   -> _vm.putHeapDouble(typedAddress.address, value.asFloat());
            case Integer -> _vm.putHeapDouble(typedAddress.address, value.asInteger());
        }
        return true;
    }

    public long allocateSymbol(NumericType type, String name) {
        var typedAddress = _symbolAddresses.getOrDefault(name, null);
        if (typedAddress != null)
            return typedAddress.address;
        var freeAddress = _freeAddresses.pop();
        _freeAddresses.push(freeAddress + 8);
        _symbolAddresses.put(name, new TypedAddress(type, freeAddress));
        return freeAddress;
    }
}
