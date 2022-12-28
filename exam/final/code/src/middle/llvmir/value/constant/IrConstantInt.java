package middle.llvmir.value.constant;

import middle.llvmir.type.IrValueType;

import java.util.ArrayList;

/**
 * 数值常数常量
 */
public class IrConstantInt extends IrConstant {
    private int val; // 常量值
    private String name; // 常量名

    public IrConstantInt(IrValueType type, int val) {
        super(type);
        this.val = val;
        /* TODO : name */
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(String.valueOf(this.val));
        return ret;
    }
}
