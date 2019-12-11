package io.nybbles.progcalc.shell.contracts;

public interface Options {
    void resetToDefaults();

    String getConfigPath();

    boolean parse(final String[] args);

    String getProperty(final String name);

    void removeProperty(final String name);
}
