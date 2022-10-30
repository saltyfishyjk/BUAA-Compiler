package middle.llvmir.type;

import java.util.ArrayList;

/**
 * LLVM IR Void Type
 * 由于Void类型不保存其他有效信息
 */
public class IrVoidType extends IrValueType {
    private static IrVoidType voidType = new IrVoidType();

    private IrVoidType() {}

    public static IrVoidType getVoidType() {
        return voidType;
    }

    @Override
    public ArrayList<String> irOutput() {
        /* TODO : 待施工 */
        return super.irOutput();
    }
}
