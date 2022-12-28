package middle.llvmir.value.instructions.terminator;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrVoidType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

import java.util.ArrayList;

/**
 * Ret -> ret <type> <value>` ,`ret void`
 * LLVM IR Ret 函数返回指令，分为返回值和返回void两种情况
 * - 返回值
 *      type : Value类型
 *      value : 值
 * - 返回void
 */
public class IrRet extends IrInstruction {
    private boolean isVoid;

    /**
     * 返回值
     * @param val 返回的值
     */
    public IrRet(IrValue val) {
        // TODO : check IrInstructionType
        super(IrInstructionType.Ret, val.getValueType(), 1);
        this.setOperand(val, 0);
        this.isVoid = false;
    }

    /**
     * 返回void
     */
    public IrRet() {
        super(IrInstructionType.Ret, IrVoidType.getVoidType(), 0);
        this.isVoid = true;
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (this.isVoid) {
            sb.append("ret void\n");
        } else {
            sb.append("ret i32 " + this.getOperand(0).getName() + "\n");
        }
        ret.add(sb.toString());
        return ret;
    }

    public boolean isVoid() {
        return isVoid;
    }
}
