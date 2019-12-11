package io.nybbles.progcalc.vm;

public interface Numeric {
    NumericType getType();

    long asInteger();

    double asFloat();

    //

    Numeric add(Numeric value);

    Numeric add(long immediate);

    Numeric add(double immediate);

    //

    Numeric sub(Numeric value);

    Numeric sub(long immediate);

    Numeric sub(double immediate);

    //

    Numeric mul(Numeric value);

    Numeric mul(long immediate);

    Numeric mul(double immediate);

    //

    Numeric div(Numeric value);

    Numeric div(long immediate);

    Numeric div(double immediate);

    //

    Numeric mod(Numeric value);

    Numeric mod(long immediate);

    Numeric mod(double immediate);

    //

    Numeric pow(Numeric value);

    Numeric pow(long immediate);

    Numeric pow(double immediate);

    //

    Numeric neg();

    //

    Numeric not();

    //

    Numeric and(Numeric value);

    Numeric and(long immediate);

    Numeric and(double immediate);

    //

    Numeric or(Numeric value);

    Numeric or(long immediate);

    Numeric or(double immediate);

    //

    Numeric xor(Numeric value);

    Numeric xor(long immediate);

    Numeric xor(double immediate);

    //

    Numeric shr(Numeric value);

    Numeric shr(long immediate);

    Numeric shr(double immediate);

    //

    Numeric shl(Numeric value);

    Numeric shl(long immediate);

    Numeric shl(double immediate);

    //

    Numeric rol(Numeric value);

    Numeric rol(long immediate);

    Numeric rol(double immediate);

    //

    Numeric ror(Numeric value);

    Numeric ror(long immediate);

    Numeric ror(double immediate);

    //

    void setInteger(long value);

    void setFloat(double value);

    void setNumeric(Numeric value);
}
