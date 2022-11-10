package middle.llvmir.type;

import java.util.ArrayList;

/**
 * LLVM IR Label Type
 * 标签类型，没有储存额外的信息，因此使用单例模式
 * 值得注意的是，LabelType是[基本块]的值类型
 */
public class IrLabelType extends IrValueType {
    private static IrLabelType labelType = new IrLabelType();

    private IrLabelType() {}

    public static IrLabelType getLabelType() {
        return labelType;
    }

    @Override
    public ArrayList<String> irOutput() {
        /* TODO : 本次作业不涉及跳转 */
        return super.irOutput();
    }
}
