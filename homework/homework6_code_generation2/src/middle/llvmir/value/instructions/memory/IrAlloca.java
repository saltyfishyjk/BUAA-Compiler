package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
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
        StringBuilder sb = new StringBuilder();
        sb.append(this.irValue.getName() + " = alloca i32");
        /*if (this.allocated instanceof IrIntegerType) {
            // 零维
            sb.append(this.irValue.getName() + " = alloca i32\n");
            ret.add(sb.toString());
        }*/
        int dimension = this.getDimension();
        if (dimension == 0) {
            // 0维
        } else if (dimension == 1) {
            // 1维
            sb.append("[" + this.getDimension1() + "]");
        } else if (dimension == 2) {
            // 2维
            sb.append("[" + this.getDimension1() + "]");
            sb.append("[" + this.getDimension2() + "]");
        } else {
            System.out.println("ERROR IN IrAlloca : should not reach here");
        }
        sb.append("\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
