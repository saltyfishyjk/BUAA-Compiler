package middle.llvmir.value.instructions.memory;

import middle.llvmir.type.IrPointerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

/**
 * alloca : <result> = alloca <type>
 * LLVM IR Alloca 内存申请指令，是pointer类型
 *
 */
public class IrAlloca extends IrInstruction {
    private boolean isInit = false; // 是否初始化
    private IrValueType allocated;

    public IrAlloca(IrValueType allocated) {
        super(IrInstructionType.Alloca, new IrPointerType(allocated), 0);
        this.allocated = allocated;
    }

    public void setInit(boolean init) {
        this.isInit = init;
    }
}
