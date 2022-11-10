package middle.llvmir.value.instructions;

import java.util.ArrayList;

/**
 * LLVM IR指令列表
 */
public class IrInstructionList {
    private ArrayList<IrInstruction> instructions;

    public IrInstructionList() {
        this.instructions = new ArrayList<>();
    }

    public void addIrInstruction(IrInstruction instruction) {
        this.instructions.add(instruction);
    }
}
