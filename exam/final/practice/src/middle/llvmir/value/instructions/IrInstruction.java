package middle.llvmir.value.instructions;

import middle.llvmir.IrUser;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.IrNode;
import middle.llvmir.value.basicblock.IrBasicBlock;

import java.util.ArrayList;

/**
 * LLVM IR Instruction 指令
 */
public class IrInstruction extends IrUser implements IrNode {
    private IrInstructionType instructionType; // 指令类型
    private IrBasicBlock basicBlock; // 指令所在基本块

    public IrInstruction(IrInstructionType instructionType,
                         IrValueType valueType,
                         int numOp) {
        super(valueType, numOp);
        this.instructionType = instructionType;
    }

    public IrInstruction(IrInstructionType instructionType,
                         IrValueType valueType,
                         int numOp,
                         IrBasicBlock basicBlock) {
        this(instructionType, valueType, numOp);
        this.basicBlock = basicBlock;
    }

    public void setInstructionType(IrInstructionType instructionType) {
        this.instructionType = instructionType;
    }

    /* 判断是否是二元逻辑运算，用于确定IrValueType与位宽 */
    /* ordinal() 是Java 枚举类获取序数的方法 */
    public boolean isLogicBinary() {
        return this.instructionType.ordinal() >= IrInstructionType.Lt.ordinal() &&
                this.instructionType.ordinal() <= IrInstructionType.Or.ordinal();
    }

    /* 判断是否位二元算术运算，用于确定IrValueType与位宽 */
    public boolean isArithmeticBinary() {
        return this.instructionType.ordinal() >= IrInstructionType.Add.ordinal() &&
                this.instructionType.ordinal() <= IrInstructionType.Div.ordinal();
    }

    @Override
    public ArrayList<String> irOutput() {
        /* TODO : 待施工 */
        return null;
    }

    public IrInstructionType getInstructionType() {
        return this.instructionType;
    }

}
