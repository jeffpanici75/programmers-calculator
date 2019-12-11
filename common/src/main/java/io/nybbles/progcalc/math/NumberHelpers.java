package io.nybbles.progcalc.math;

import java.util.Arrays;

public final class NumberHelpers {
    private static char[] s_octChars = {'0', '1', '2', '3', '4', '5', '6', '7'};
    private static char[] s_hexChars = {
            '0', '1',
            '2', '3',
            '4', '5',
            '6', '7',
            '8', '9',
            'A', 'B',
            'C', 'D',
            'E', 'F'};

    public static String octalStringForSize(long value, int size) {
        var buffer = new char[size];
        Arrays.fill(buffer, ' ');
        var pos = size - 1;
        while (size > 0) {
            var octal = (int) (value & 0b111);
            buffer[pos--] = s_octChars[octal];
            value >>>= 3;
            size -= 2;
        }
        return new String(buffer);
    }

    public static String hexStringForSize(long value, int size) {
        var buffer = new char[size];
        Arrays.fill(buffer, ' ');
        var i = size - 1;
        while (size > 0) {
            var nybble = (int) (value & 0x0f);
            buffer[i--] = s_hexChars[nybble];
            value >>>= 4;
            size -= 4;
        }
        return new String(buffer);
    }

    public static String binaryStringForSize(long value, int size) {
        var buffer = new char[size];
        for (int i = 0; i < size; i++) {
            var mask = 1L << i;
            if ((value & mask) == mask) {
                buffer[(size - 1) - i] = '1';
            } else {
                buffer[(size - 1) - i] = '0';
            }
        }
        return new String(buffer);
    }
}
