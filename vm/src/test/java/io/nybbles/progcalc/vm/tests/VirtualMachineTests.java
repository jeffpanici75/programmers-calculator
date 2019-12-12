package io.nybbles.progcalc.vm.tests;

import io.nybbles.progcalc.common.Result;
import io.nybbles.progcalc.vm.RegisterName;
import io.nybbles.progcalc.vm.VirtualMachine;
import org.junit.Before;
import org.junit.Test;

import static io.nybbles.progcalc.vm.RegisterName.*;
import static org.junit.Assert.*;

public class VirtualMachineTests {
    private VirtualMachine _vm;

    @Before
    public void testSetup() {
        var r = new Result();
        _vm = new VirtualMachine(65535, 4096, 4096);
        _vm.addTrapHandler(0x1f, (result, vm) -> {
            var numberOfArguments = vm.pop();
            var arguments = new Object[(int)numberOfArguments];
            var format = new StringBuilder();
            format.append("TRAP: ");
            for (int i = 0; i < numberOfArguments; i++) {
                if (i > 0) format.append(" ");
                arguments[i] = vm.pop();
                format.append("0x%X");
            }
            format.append("\n");
            System.out.format(format.toString(), arguments);
            return true;
        });
        assertTrue(_vm.initialize(r));
    }

    @Test
    public void basicStackOperations() {
        assertEquals(65535, _vm.getHeapSize());
        assertEquals(4096, _vm.getStackSize());
        assertEquals(4096, _vm.getDataSize());

        _vm.push(10L);
        _vm.push(20L);
        _vm.push(3.14);

        assertEquals(3.14, _vm.popDouble(), 0.0);
        assertEquals(20L, _vm.pop());
        assertEquals(10L, _vm.pop());
    }

    @Test
    public void emitByteCode() {
        var r = new Result();

        var emitter = _vm.getEmitter(0);
        emitter
            .label("int_data")
                .dq(512)
                .dq(256)
                .dq(128)
                .dq(64)
                .dq(32)
                .dq(16)
                .dq(8)
            .label("sum_storage")
                .dq(0)
            .label("float_data")
                .dq(3.14)
                .dq(1.16)
                .dq(9.1445)
            .align(16)
            .label("start")
                .push(10L)
                .move(I0, 20L)
                .push(I0)
                .move(F0, 3.14)
                .push(F0)
                .pop(F1)
                .pop(I1)
                .add(F2, F1, F1)
                .mul(I2, I1, I1)
                .clr(I1)
                .move(I6, 0L)
                .move(I7, 8L)
                .move(I0, 7L)
                .move(I2, "int_data")
                .label("loop")
                    .load(I2, I3)
                    .add(I2, I2, I7)
                    .add(I1, I1, I3)
                    .push(I1)
                    .push(1L)
                    .trap(0x1f)
                    .dec(I0)
                    .cmp(I5, I0, I6)
                    .bne(I5, "loop")
                .move(I2, "sum_storage")
                .store(I2, I1)
                .push(I1)
                .push(1L)
                .trap(0x1f)
                .exit();
        assertTrue(emitter.emit(r));
        assertEquals(576, emitter.getAddress());

        var sumStorageAddress = emitter.getLabelAddress("sum_storage");
        var startAddress = emitter.getLabelAddress("start");
        var endAddress = emitter.getAddress();
        var reader = _vm.getReader(startAddress, endAddress);
        var program = reader.read();
        assertEquals(29, program.instructionCount());

        _vm.setProgram(program);

        while (true) {
            var success = _vm.step(r);
            if (!success)
                break;
        }

        assertTrue(r.isSuccess());

        var i1 = _vm.getRegister(RegisterName.I1);
        assertEquals(1016, i1.getValue().asInteger());

        var sum = _vm.getHeapLong(sumStorageAddress);
        assertEquals(1016, sum);
    }
}
