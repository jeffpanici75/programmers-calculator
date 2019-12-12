package io.nybbles.progcalc.shell.contracts;

import io.nybbles.progcalc.common.Result;

public interface DeferredTrap {
    boolean execute(Result r, TrapContext context);
}
