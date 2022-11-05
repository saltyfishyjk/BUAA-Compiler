package backend.instruction;

import backend.basicblock.MipsBasicBlock;
import middle.llvmir.value.instructions.IrInstruction;

/**
 * Mips Instruction Builder : Mips指令生成器
 */
public class MipsInstructionBuilder {
    private IrInstruction irInstruction;
    private MipsBasicBlock father; // 父BasicBlock

    public MipsInstructionBuilder(MipsBasicBlock father, IrInstruction irInstruction) {
        this.irInstruction = irInstruction;
        this.father = father;
    }

    public MipsInstruction genMipsInstruction() {
        /* TODO : 待施工 */
        return null;
    }
}
