package io.nybbles.progcalc.shell.gui;

public final class StringHelpers {
    public static String padString(String value, int width) {
        value += " ".repeat(width - value.length());
        return value;
    }
}
