package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

import java.util.ArrayList;

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

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + " = load i32, i32* " + this.getOperand(0).getName() + "\n");
        ret.add(sb.toString());
        return ret;
    }
}
