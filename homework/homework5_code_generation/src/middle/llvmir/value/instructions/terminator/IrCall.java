package middle.llvmir.value.instructions.terminator;

import middle.llvmir.value.IrFunction;
import middle.llvmir.value.instructions.IrInstruction;

/**
 * Call -> <result> = call [ret attrs] <ty> <fnptrval>(<function args>)
 * ty:type Value类型
 * fnptrval:FunctionPointerValue 为一个要调用函数的指针
 * function args : 函数参数
 */
public class IrCall extends IrInstruction {
    public Call(IrFunction function, )
}
