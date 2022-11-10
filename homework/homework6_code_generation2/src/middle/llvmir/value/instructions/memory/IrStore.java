package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrVoidType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

import java.util.ArrayList;

/**
 * store : store <ty> <value>, <ty>* <pointer>
 * value : 被存的值
 * pointer : 存向的目标
 * LLVM IR Store 内存存储指令
 */
public class IrStore extends IrInstruction {
    public IrStore(IrValue value, IrValue pointer) {
        super(IrInstructionType.Store, IrVoidType.getVoidType(), 2);
        this.setOperand(value, 0);
        this.setOperand(pointer, 1);
    }

    @Override
    public ArrayList<String> irOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append("store i32 "); // TODO : 这里应当只有i32一种存储
        IrValue operand1 = this.getOperand(0);
        IrValue operand2 = this.getOperand(1);
        sb.append(operand1.getName() + ", i32* ");
        sb.append(operand2.getName() + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
