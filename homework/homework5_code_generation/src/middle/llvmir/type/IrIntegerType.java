package middle.llvmir.type;

/**
 * LLVM IR Integer Type
 * numBits 标识位宽，在SysY中，只用得到1和32两种，分别表示逻辑和32位整数
 */
public class IrIntegerType extends IrValueType {
    private int numBits;
    private static final IrIntegerType I32 = new IrIntegerType(32);
    private static final IrIntegerType I1 = new IrIntegerType(1);

    /* 设为私有方法，以避免外部调用生成不符合语义的IrIntegerType */
    private IrIntegerType(int numBits) {
        this.numBits = numBits;
    }

    public static IrIntegerType get32() {
        return I32;
    }

    public static IrIntegerType get1() {
        return I1;
    }
}
