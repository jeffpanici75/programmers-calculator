package io.nybbles.progcalc.shell.contracts;

import java.io.InputStream;
import java.io.OutputStream;

public interface Configuration {
    void load();

    void save();

    void clearProperties();

    void load(InputStream inputStream);

    void save(OutputStream outputStream);
}
