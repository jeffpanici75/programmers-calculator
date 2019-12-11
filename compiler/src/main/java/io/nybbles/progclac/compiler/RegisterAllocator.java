package io.nybbles.progclac.compiler;

import java.util.Stack;
import io.nybbles.progcalc.vm.RegisterName;
import io.nybbles.progcalc.vm.RegisterType;

public class RegisterAllocator {
    private static RegisterName[] s_registerNames;
    private Stack<RegisterName> _intRegs = new Stack<>();
    private Stack<RegisterName> _floatRegs = new Stack<>();

    static {
        s_registerNames = RegisterName.values();
    }

    public RegisterAllocator() {
        for (int i = 15; i >= 0; --i) {
            _intRegs.push(s_registerNames[3 + i]);
            _floatRegs.push(s_registerNames[19 + i]);
        }
    }

    public void releaseRegister(RegisterName register) {
        var ordinal = register.ordinal();
        if (ordinal >= 3 && ordinal <= 18)
            _intRegs.push(register);
        else
            _floatRegs.push(register);
    }

    public RegisterName allocateRegister(RegisterType type) {
        if (type == RegisterType.U64)
            return _intRegs.pop();
        else if (type == RegisterType.F64)
            return _floatRegs.pop();
        else
            return null;
    }
}
