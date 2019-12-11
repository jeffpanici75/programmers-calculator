package io.nybbles.progcalc.vm;

public class IntegerNumeric implements Numeric {
    private long _value;

    public IntegerNumeric(long value) {
        _value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Numeric)) return false;
        return _value == ((Numeric)o).asInteger();
    }

    @Override
    public NumericType getType() {
        return NumericType.Integer;
    }

    @Override
    public long asInteger() {
        return _value;
    }

    @Override
    public double asFloat() {
        return (double) _value;
    }

    //

    @Override
    public void setInteger(long value) {
        _value = value;
    }

    @Override
    public void setFloat(double value) {
        _value = (long) value;
    }

    @Override
    public void setNumeric(Numeric value) {
        _value = value.asInteger();
    }

    //

    @Override
    public Numeric add(Numeric value) {
        return new IntegerNumeric(_value + value.asInteger());
    }

    @Override
    public Numeric add(long immediate) {
        return new IntegerNumeric(_value + immediate);
    }

    @Override
    public Numeric add(double immediate) {
        return new IntegerNumeric(_value + (long) immediate);
    }

    //

    @Override
    public Numeric sub(Numeric value) {
        return new IntegerNumeric(_value - value.asInteger());
    }

    @Override
    public Numeric sub(long immediate) {
        return new IntegerNumeric(_value - immediate);
    }

    @Override
    public Numeric sub(double immediate) {
        return new IntegerNumeric(_value - (long) immediate);
    }

    //

    @Override
    public Numeric mul(Numeric value) {
        return new IntegerNumeric(_value * value.asInteger());
    }

    @Override
    public Numeric mul(long immediate) {
        return new IntegerNumeric(_value * immediate);
    }

    @Override
    public Numeric mul(double immediate) {
        return new IntegerNumeric(_value * (long) immediate);
    }

    //

    @Override
    public Numeric div(Numeric value) {
        return new IntegerNumeric(_value / value.asInteger());
    }

    @Override
    public Numeric div(long immediate) {
        return new IntegerNumeric(_value / immediate);
    }

    @Override
    public Numeric div(double immediate) {
        return new IntegerNumeric(_value / (long) immediate);
    }

    //

    @Override
    public Numeric mod(Numeric value) {
        return new IntegerNumeric(_value % value.asInteger());
    }

    @Override
    public Numeric mod(long immediate) {
        return new IntegerNumeric(_value % immediate);
    }

    @Override
    public Numeric mod(double immediate) {
        return new IntegerNumeric(_value % (long) immediate);
    }

    //

    @Override
    public Numeric pow(Numeric value) {
        return new IntegerNumeric((long) Math.pow(_value, value.asInteger()));
    }

    @Override
    public Numeric pow(long immediate) {
        return new IntegerNumeric((long) Math.pow(_value, immediate));
    }

    @Override
    public Numeric pow(double immediate) {
        return new IntegerNumeric((long) Math.pow(_value, immediate));
    }

    //

    @Override
    public Numeric neg() {
        return new IntegerNumeric(-_value);
    }

    //

    @Override
    public Numeric not() {
        return new IntegerNumeric(~_value);
    }

    //

    @Override
    public Numeric and(Numeric value) {
        return new IntegerNumeric(_value & value.asInteger());
    }

    @Override
    public Numeric and(long immediate) {
        return new IntegerNumeric(_value & immediate);
    }

    @Override
    public Numeric and(double immediate) {
        return new IntegerNumeric(_value & (long) immediate);
    }

    //

    @Override
    public Numeric or(Numeric value) {
        return new IntegerNumeric(_value | value.asInteger());
    }

    @Override
    public Numeric or(long immediate) {
        return new IntegerNumeric(_value | immediate);
    }

    @Override
    public Numeric or(double immediate) {
        return new IntegerNumeric(_value | (long) immediate);
    }

    //

    @Override
    public Numeric xor(Numeric value) {
        return new IntegerNumeric(_value ^ value.asInteger());
    }

    @Override
    public Numeric xor(long immediate) {
        return new IntegerNumeric(_value ^ immediate);
    }

    @Override
    public Numeric xor(double immediate) {
        return new IntegerNumeric(_value ^ (long) immediate);
    }

    //

    @Override
    public Numeric shr(Numeric value) {
        return new IntegerNumeric(_value >> value.asInteger());
    }

    @Override
    public Numeric shr(long immediate) {
        return new IntegerNumeric(_value >> immediate);
    }

    @Override
    public Numeric shr(double immediate) {
        return new IntegerNumeric(_value >> (long) immediate);
    }

    //

    @Override
    public Numeric shl(Numeric value) {
        return new IntegerNumeric(_value << value.asInteger());
    }

    @Override
    public Numeric shl(long immediate) {
        return new IntegerNumeric(_value << immediate);
    }

    @Override
    public Numeric shl(double immediate) {
        return new IntegerNumeric(_value << (long) immediate);
    }

    //

    @Override
    public Numeric rol(Numeric value) {
        return new IntegerNumeric((_value << value.asInteger()) | (_value >> (Long.SIZE - value.asInteger())));
    }

    @Override
    public Numeric rol(long immediate) {
        return new IntegerNumeric((_value << immediate) | (_value >> (Long.SIZE - immediate)));
    }

    @Override
    public Numeric rol(double immediate) {
        return new IntegerNumeric( (_value << (long) immediate) | (_value >> (Long.SIZE - (long) immediate)));
    }

    //

    @Override
    public Numeric ror(Numeric value) {
        return new IntegerNumeric( (_value >> value.asInteger()) | (_value << (Long.SIZE - value.asInteger())));
    }

    @Override
    public Numeric ror(long immediate) {
        return new IntegerNumeric( (_value >> immediate) | (_value << (Long.SIZE - immediate)));
    }

    @Override
    public Numeric ror(double immediate) {
        return new IntegerNumeric( (_value >> (long) immediate) | (_value << (Long.SIZE - (long) immediate)));
    }
}
