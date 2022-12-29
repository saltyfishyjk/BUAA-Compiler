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
            // sb.append("%_LocalVariable" + this.rank);
            sb.append(this.getName());
        } else if (this.getValueType() instanceof IrArrayType) {
            /* 数组 */
            int dimension = this.getDimension();
            if (dimension == 1) {
                /* 形如a[] */
                sb.append("i32* ");
                // sb.append("%_LocalVariable" + this.rank);
                sb.append(this.getName());
                sb.append("[] ");
            } else if (dimension == 2) {
                /* 形如a[][constExp] */
                sb.append("i32** ");
                sb.append(this.getName());
                sb.append("[]");
                sb.append("[" + this.getDimension2() + "] ");
            }
        }
        ret.add(sb.toString());
        return ret;
    }

    public int getRank() {
        return rank;
    }
}
