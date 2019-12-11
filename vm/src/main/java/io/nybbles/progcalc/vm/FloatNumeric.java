package io.nybbles.progcalc.vm;

public class FloatNumeric implements Numeric {
    public double _value;

    public FloatNumeric(double value) {
        _value = value;

    }

    @Override
    public NumericType getType() {
        return NumericType.Float;
    }

    @Override
    public long asInteger() {
        return (long) _value;
    }

    @Override
    public double asFloat() {
        return _value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Numeric)) return false;
        return _value == ((Numeric)o).asFloat();
    }

    //

    @Override
    public void setInteger(long value) {
        _value = (double) value;
    }

    @Override
    public void setFloat(double value) {
        _value = value;
    }

    @Override
    public void setNumeric(Numeric value) {
        _value = value.asFloat();
    }

    //

    @Override
    public Numeric add(Numeric value) {
        return new FloatNumeric(_value + value.asFloat());
    }

    @Override
    public Numeric add(long immediate) {
        return new FloatNumeric(_value + immediate);
    }

    @Override
    public Numeric add(double immediate) {
        return new FloatNumeric(_value + immediate);
    }

    //

    @Override
    public Numeric sub(long immediate) {
        return new FloatNumeric(_value - immediate);
    }

    @Override
    public Numeric sub(double immediate) {
        return new FloatNumeric(_value - immediate);
    }

    @Override
    public Numeric sub(Numeric value) {
        return new FloatNumeric(_value - value.asFloat());
    }

    //

    @Override
    public Numeric mul(Numeric value) {
        return new FloatNumeric(_value * value.asFloat());
    }

    @Override
    public Numeric mul(long immediate) {
        return new FloatNumeric(_value * immediate);
    }

    @Override
    public Numeric mul(double immediate) {
        return new FloatNumeric(_value * immediate);
    }

    //

    @Override
    public Numeric div(Numeric value) {
        return new FloatNumeric(_value / value.asFloat());
    }

    @Override
    public Numeric div(long immediate) {
        return new FloatNumeric(_value / immediate);
    }

    @Override
    public Numeric div(double immediate) {
        return new FloatNumeric(_value / immediate);
    }

    //

    @Override
    public Numeric mod(Numeric value) {
        return new FloatNumeric(_value % value.asInteger());
    }

    @Override
    public Numeric mod(long immediate) {
        return new FloatNumeric(_value % immediate);
    }

    @Override
    public Numeric mod(double immediate) {
        return new FloatNumeric(_value % immediate);
    }

    //

    @Override
    public Numeric pow(Numeric value) {
        return new FloatNumeric(Math.pow(_value, value.asFloat()));
    }

    @Override
    public Numeric pow(long immediate) {
        return new FloatNumeric(Math.pow(_value, immediate));
    }

    @Override
    public Numeric pow(double immediate) {
        return new FloatNumeric(Math.pow(_value, immediate));
    }

    //

    @Override
    public Numeric neg() {
        return new FloatNumeric(-_value);
    }

    //

    @Override
    public Numeric not() {
        var newValue = Double.longBitsToDouble(~Double.doubleToRawLongBits(_value));
        return new FloatNumeric(newValue);
    }

    //

    @Override
    public Numeric and(Numeric value) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) & value.asInteger());
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric and(long immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) & immediate);
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric and(double immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) & (long) immediate);
        return new FloatNumeric(newValue);
    }

    //

    @Override
    public Numeric or(Numeric value) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) | value.asInteger());
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric or(long immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) | immediate);
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric or(double immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) | (long) immediate);
        return new FloatNumeric(newValue);
    }

    //

    @Override
    public Numeric xor(Numeric value) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) ^ value.asInteger());
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric xor(long immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) ^ immediate);
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric xor(double immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) ^ (long) immediate);
        return new FloatNumeric(newValue);
    }

    //

    @Override
    public Numeric shr(Numeric value) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) >> value.asInteger());
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric shr(long immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) >> immediate);
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric shr(double immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) >> (long) immediate);
        return new FloatNumeric(newValue);
    }

    //

    @Override
    public Numeric shl(Numeric value) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) << value.asInteger());
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric shl(long immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) << immediate);
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric shl(double immediate) {
        var newValue = Double.longBitsToDouble(Double.doubleToRawLongBits(_value) << (long) immediate);
        return new FloatNumeric(newValue);
    }

    //

    @Override
    public Numeric rol(Numeric value) {
        var rawBits = Double.doubleToRawLongBits(_value);
        var newValue = Double.longBitsToDouble((rawBits << value.asInteger()) | (rawBits >> (Long.SIZE - value.asInteger())));
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric rol(long immediate) {
        var rawBits = Double.doubleToRawLongBits(_value);
        var newValue = Double.longBitsToDouble((rawBits << immediate) | (rawBits >> (Long.SIZE - immediate)));
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric rol(double immediate) {
        var rawBits = Double.doubleToRawLongBits(_value);
        var newValue = Double.longBitsToDouble((rawBits << (long) immediate) | (rawBits >> (Long.SIZE - (long) immediate)));
        return new FloatNumeric(newValue);
    }

    //

    @Override
    public Numeric ror(Numeric value) {
        var rawBits = Double.doubleToRawLongBits(_value);
        var newValue = Double.longBitsToDouble((rawBits >> value.asInteger()) | (rawBits << (Long.SIZE - value.asInteger())));
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric ror(long immediate) {
        var rawBits = Double.doubleToRawLongBits(_value);
        var newValue = Double.longBitsToDouble((rawBits >> immediate) | (rawBits << (Long.SIZE - immediate)));
        return new FloatNumeric(newValue);
    }

    @Override
    public Numeric ror(double immediate) {
        var rawBits = Double.doubleToRawLongBits(_value);
        var newValue = Double.longBitsToDouble((rawBits >> (long) immediate) | (rawBits << (Long.SIZE - (long) immediate)));
        return new FloatNumeric(newValue);
    }
}
