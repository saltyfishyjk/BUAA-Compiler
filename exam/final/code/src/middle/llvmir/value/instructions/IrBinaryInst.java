package middle.llvmir.value.instructions;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrValueType;

import java.util.ArrayList;

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
        if (right != null) {
            this.setOperand(right, 1);
        }

    }

    @Override
    public ArrayList<String> irOutput() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + " = ");
        switch (this.getInstructionType()) {
            case Add:
                sb.append("add ");
                break;
            case Sub:
                sb.append("sub ");
                break;
            case Mul:
                sb.append("mul ");
                break;
            case Div:
                sb.append("sdiv ");
                break;
            case Mod:
                sb.append("srem ");
                break; 
            case Bitand:
                sb.append("and ");
            case Lt:
                sb.append("Lt ");
                break;
            case Gt:
                sb.append("Gt ");
                break;
            case Le:
                sb.append("Le ");
                break;
            case Ge:
                sb.append("Ge ");
                break;
            case Not:
                sb.append("Not ");
                break;
            case Ne:
                sb.append("Ne ");
                break;
            case Eq:
                sb.append("Eq ");
                break;
            default:
                System.out.println("ERROR in IrBinaryInst : should not reach here");
                break;
        }
        if (this.getValueType() instanceof IrIntegerType) {
            sb.append("i32 ");
        } else  {
            /* TODO : 待施工 */
        }

        sb.append(this.getOperand(0).getName());
        if (this.getOperand(1) != null && this.getInstructionType() != IrInstructionType.Not) {
            sb.append(", ");
            sb.append(this.getOperand(1).getName());
        }
        sb.append("\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }

    public IrValue getLeft() {
        return this.getOperand(0);
    }

    public IrValue getRight() {
        return this.getOperand(1);
    }

}
