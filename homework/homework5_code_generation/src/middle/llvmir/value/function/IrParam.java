package middle.llvmir.value.function;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrValueType;

/**
 * LLVM IR Param 函数形参
 */
public class IrParam extends IrValue {
    private int rank; // 标记该形参位置

    public IrParam(IrValueType valueType, int rank) {
        super(valueType);
        this.rank = rank;
    }
}
