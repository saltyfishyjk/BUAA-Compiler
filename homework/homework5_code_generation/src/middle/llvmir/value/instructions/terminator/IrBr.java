package middle.llvmir.value.instructions.terminator;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrLabelType;
import middle.llvmir.value.basicblock.IrBasicBlock;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

/* TODO : 本次作业不涉及跳转（条件和循环） */

/**
 * Br -> br i1 <cond>, label <iftrue>, label <iffalse> | br label <dest>
 * LLVM IR Br 分支跳转指令，共有两种可能，分别为有条件跳转和无条件跳转
 * cond : 条件表达式变量，为i1类型
 * - 有条件跳转
 *      iftrue : 如果cond为真跳转的label
 *      iffalse : 如果cond为假跳转的label
 * - 无条件跳转
 *      dest : 跳转目标
 */
public class IrBr extends IrInstruction {
    /**
     * 有条件跳转
     * @param cond 第0个operand
     * @param trueBlock 第1个operand
     * @param falseBlock 第2个operand
     */
    public IrBr(IrValue cond, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        super(IrInstructionType.Br, IrLabelType.getLabelType(), 3);
        this.setOperand(cond, 0);
        this.setOperand(trueBlock, 1);
        this.setOperand(falseBlock, 2);
    }

    /**
     * 无条件跳转
     * @param trueBlock : 第0个operand
     */
    public IrBr(IrBasicBlock trueBlock) {
        super(IrInstructionType.Br, IrLabelType.getLabelType(), 1);
        setOperand(trueBlock, 0);
    }

}
