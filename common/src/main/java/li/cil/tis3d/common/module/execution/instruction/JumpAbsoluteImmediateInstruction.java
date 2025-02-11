package li.cil.tis3d.common.module.execution.instruction;

import li.cil.tis3d.common.module.execution.Machine;

public class JumpAbsoluteImmediateInstruction implements Instruction {
    private final short pc;

    public JumpAbsoluteImmediateInstruction(final short pc) {
        this.pc = pc;
    }

    @Override
    public void step(final Machine machine) {
        machine.getState().pc = pc;
    }

    @Override
    public String toString() {
        return JumpAbsoluteInstruction.NAME + " " + pc;
    }
}
