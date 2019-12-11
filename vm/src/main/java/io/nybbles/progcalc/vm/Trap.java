package io.nybbles.progcalc.vm;

import io.nybbles.progcalc.common.Result;

public interface Trap {
    boolean execute(Result r, VirtualMachine vm);
}
