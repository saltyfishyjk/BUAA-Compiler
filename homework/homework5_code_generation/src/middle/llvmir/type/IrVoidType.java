package middle.llvmir.type;

/**
 * LLVM IR Void Type
 * 由于Void类型不保存其他有效信息
 */
public class IrVoidType extends IrValueType {
    private static IrVoidType voidType = new IrVoidType();

    private IrVoidType() {}

    public IrVoidType getVoidType() {
        return voidType;
    }

}
