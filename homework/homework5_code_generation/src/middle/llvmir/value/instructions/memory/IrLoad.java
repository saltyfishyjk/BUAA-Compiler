package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

/**
 * load : <result> = load <ty>, <ty>* <pointer>
 * LLVM IR Load 内存读取指令
 * result : 内存读取后储存变量
 * ty : 变量类型，在本实验中只有i32
 * pointer :
 */
public class IrLoad extends IrInstruction {
    public IrLoad(IrValueType valueType, IrValue value) {
        super(IrInstructionType.Load, valueType, 1);
        this.setOperand(value, 0);
    }
}
