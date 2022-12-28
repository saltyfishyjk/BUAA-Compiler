package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

/**
 * zext..to : <result> = zext <ty> <value> to <ty2>
 * LLVM IR Zext Zero Extend 类型转换，用于进行0拓展，从i1（一般在比较结果中出现）转换到i32需要用到zext
 */
public class IrZext extends IrInstruction {
    private IrValueType targetType; // 目标类型，在本实验中只有i32

    public IrZext(IrValue value, IrValueType targetType) {
        super(IrInstructionType.Zext, targetType, 1);
        this.targetType = targetType;
        this.setOperand(value, 0);
    }
}
