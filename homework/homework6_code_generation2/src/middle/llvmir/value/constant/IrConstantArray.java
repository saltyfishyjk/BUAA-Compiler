package middle.llvmir.value.constant;

import middle.llvmir.type.IrValueType;

import java.util.ArrayList;

/* TODO : 本次作业不涉及数组 */
/**
 * 数组数值常量
 */
public class IrConstantArray extends IrConstant {
    private ArrayList<IrConstant> constants;

    public IrConstantArray(IrValueType type, ArrayList<IrConstant> arr) {
        super(type, arr.size());
        int len = arr.size();
        for (int i = 0; i < len; i++) {
            this.setOperand(arr.get(i), i);
        }
    }

    @Override
    public ArrayList<String> irOutput() {
        /* TODO : 本次作业不涉及数组 */
        return super.irOutput();
    }
}
