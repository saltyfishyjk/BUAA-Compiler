package middle.llvmir.type;

import java.util.ArrayList;

/**
 * LLVM IR Pointer Type
 * 指针类型，gv, alloca, gep 指令都是指针类型
 */
public class IrPointerType extends IrValueType {
    private IrValueType contained; // 指针的类型

    public IrPointerType(IrValueType contained) {
        this.contained = contained;
    }

    public IrValueType getContained() {
        return contained;
    }

    @Override
    public ArrayList<String> irOutput() {
        /* TODO : 待施工 */
        return super.irOutput();
    }
}
