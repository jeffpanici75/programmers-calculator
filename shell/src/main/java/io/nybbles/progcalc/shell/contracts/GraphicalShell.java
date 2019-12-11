package io.nybbles.progcalc.shell.contracts;

import io.nybbles.progcalc.common.Result;

public interface GraphicalShell {
    void run();

    boolean initialize(Result r);
}
