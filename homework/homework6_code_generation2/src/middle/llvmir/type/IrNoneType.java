package middle.llvmir.type;

import java.util.ArrayList;

/**
 * LLVM IR None Type
 * 用于没有ValueType的Value
 * 类似VoidType，其并没有储存更多信息，因此使用单例模式
 */
public class IrNoneType extends IrValueType {
    private static IrNoneType noneType = new IrNoneType();

    private IrNoneType() {}

    public static IrNoneType getNoneType() {
        return noneType;
    }

    @Override
    public ArrayList<String> irOutput() {
        /* TODO : 待施工 */
        return super.irOutput();
    }
}
