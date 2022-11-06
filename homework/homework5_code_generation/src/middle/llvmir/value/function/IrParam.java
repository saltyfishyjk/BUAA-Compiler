package middle.llvmir.value.function;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrArrayType;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.IrNode;

import java.util.ArrayList;

/**
 * LLVM IR Param 函数形参
 */
public class IrParam extends IrValue implements IrNode {
    private int rank; // 标记该形参位置

    public IrParam(IrValueType valueType, int rank) {
        super(valueType);
        this.rank = rank;
    }

    public IrParam(IrValueType valueType, int rank, String name) {
        this(valueType, rank);
        this.setName(name);
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (this.getValueType() instanceof IrIntegerType) {
            sb.append("i32 ");
        } else if (this.getValueType() instanceof IrArrayType) {
            /* TODO : 本次作业不涉及数组 */
        }
        sb.append("%_LocalVariable" + this.rank);
        ret.add(sb.toString());
        return ret;
    }

    public int getRank() {
        return rank;
    }
}
