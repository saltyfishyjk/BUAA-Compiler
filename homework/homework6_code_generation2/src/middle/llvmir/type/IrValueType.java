package middle.llvmir.type;

import middle.llvmir.value.IrNode;

import java.util.ArrayList;

/**
 * 是所有类型的基类
 */
public class IrValueType implements IrNode {
    @Override
    public ArrayList<String> irOutput() {
        return new ArrayList<>();
    }
}
