package backend.basicblock;

import backend.function.MipsFunction;
import backend.instruction.MipsInstructionBuilder;
import middle.llvmir.value.basicblock.IrBasicBlock;
import middle.llvmir.value.instructions.IrInstruction;

import java.util.ArrayList;

/**
 * Mips Basic Block Builder : Mips基本块生成器
 */
public class MipsBasicBlockBuilder {
    private IrBasicBlock basicBlock;
    private MipsFunction father; // 父Function

    public MipsBasicBlockBuilder(MipsFunction father, IrBasicBlock basicBlock) {
        this.basicBlock = basicBlock;
        this.father = father;
    }

    public MipsBasicBlock genMipsBasicBlock() {
        MipsBasicBlock block = new MipsBasicBlock(father);
        ArrayList<IrInstruction> instructions = basicBlock.getInstructions();
        for (IrInstruction instruction : instructions) {
            MipsInstructionBuilder builder = new MipsInstructionBuilder(block, instruction);
            block.addInstruction(builder.genMipsInstruction());
        }
        return block;
    }

}
