package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrPointerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

import java.util.ArrayList;

/**
 * alloca : <result> = alloca <type>
 * LLVM IR Alloca 内存申请指令，是pointer类型
 *
 */
public class IrAlloca extends IrInstruction {
    private boolean isInit = false; // 是否初始化
    private IrValueType allocated;
    private IrValue irValue; // 本指令的操作对象

    public IrAlloca(IrValueType allocated) {
        super(IrInstructionType.Alloca, new IrPointerType(allocated), 0);
        this.allocated = allocated;
    }

    public IrAlloca(IrValueType allocated, IrValue irValue) {
        this(allocated);
        this.irValue = irValue;
    }

    public void setInit(boolean init) {
        this.isInit = init;
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (this.allocated instanceof IrIntegerType) {
            // 零维
            sb.append(this.irValue.getName() + " = alloca i32\n");
            ret.add(sb.toString());
        }
        return ret;
    }
}
