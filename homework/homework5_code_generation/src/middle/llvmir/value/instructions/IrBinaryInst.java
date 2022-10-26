package middle.llvmir.value.instructions;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrValueType;

/**
 * Binary Calculation
 * <result> = <op> <ty> <op1>, <op2>
 * <op>包括 :
 * - Add +
 * - Sub -
 * - Mul *
 * - Div /
 * - Lt < Less Than
 * - Le <= Less or Equal
 * - Ge >= Greater or Equal
 * - Gt > Greater
 * - Eq == Equal
 * - Ne != Not Equal
 * - And &
 * - Or |
 * ty : Type Value类型
 * op1, op2 : 操作数
 */
public class IrBinaryInst extends IrInstruction {
    public IrBinaryInst(IrValueType valueType,
                        IrInstructionType irInstructionType,
                        IrValue left,
                        IrValue right) {
        super(irInstructionType, valueType, 2);
        /* 如果是二元算术运算，修改本指令的IrValueType为Int32 */
        if (this.isArithmeticBinary()) {
            this.setValueType(IrIntegerType.get32());
        }
        /* 如果是二元逻辑运算，修改本指令的IrValueType为Int1 */
        if (this.isLogicBinary()) {
            this.setValueType(IrIntegerType.get1());
        }
        /* 设置左操作数 */
        this.setOperand(left, 0);
        /* 设置右操作数 */
        this.setOperand(right, 1);
    }
}
