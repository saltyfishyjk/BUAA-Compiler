package middle.llvmir.type;

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
}
