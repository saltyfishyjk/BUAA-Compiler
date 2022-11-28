package middle.llvmir.value.instructions.terminator;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrLabelType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;
import middle.llvmir.value.instructions.IrLabel;

import java.util.ArrayList;

/**
 * Br -> br i1 <cond>, label <iftrue>, label <iffalse> | br label <dest>
 * LLVM IR Br 分支跳转指令，共有两种可能，分别为有条件跳转和无条件跳转
 * cond : 条件表达式变量，为i1类型
 * - 有条件跳转
 *      iftrue : 如果cond为真跳转的label
 *      iffalse : 如果cond为假跳转的label
 * - 无条件跳转
 *      dest : 跳转目标
 * 在本实验中，为了更贴合mips的特性，我们只使用无条件跳转，即，将其作为goto使用
 */
public class IrBr extends IrInstruction {
    public IrBr(IrValue op1, IrValue op2, IrLabel label, IrInstructionType type) {
        super(type, IrLabelType.getLabelType(), 3);
        this.setOperand(op1, 0);
        this.setOperand(op2, 1);
        this.setOperand(label, 2);
        this.setName(type.name());
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + " " + this.getOperand(0).getName() +
                ", " + this.getOperand(1).getName() + ", " + this.getOperand(2).getName() + "\n");
        ret.add(sb.toString());
        return ret;
    }

    public IrValue getLeft() {
        return this.getOperand(0);
    }

    public IrValue getRight() {
        return this.getOperand(1);
    }

    public IrValue getLabel() {
        return this.getOperand(2);
    }
}
