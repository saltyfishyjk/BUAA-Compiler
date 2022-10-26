package middle.llvmir.value.instructions.terminator;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrVoidType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

/**
 * Ret -> ret <type> <value>` ,`ret void`
 * LLVM IR Ret 函数返回指令，分为返回值和返回void两种情况
 * - 返回值
 *      type : Value类型
 *      value : 值
 * - 返回void
 */
public class IrRet extends IrInstruction {
    /**
     * 返回值
     * @param val 返回的值
     */
    public IrRet(IrValue val) {
        // TODO : check IrInstructionType
        super(IrInstructionType.Ret, val.getValueType(), 1);
        this.setOperand(val, 0);
    }

    /**
     * 返回void
     */
    public IrRet() {
        super(IrInstructionType.Ret, IrVoidType.getVoidType(), 0);
    }
}
