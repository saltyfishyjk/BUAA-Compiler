package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrVoidType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

/**
 * store : store <ty> <value>, <ty>* <pointer>
 * LLVM IR Store 内存存储指令
 */
public class IrStore extends IrInstruction {
    public IrStore(IrValue value, IrValue pointer) {
        super(IrInstructionType.Store, IrVoidType.getVoidType(), 2);
        this.setOperand(value, 0);
        this.setOperand(pointer, 1);
    }
}
