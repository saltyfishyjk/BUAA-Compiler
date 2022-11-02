package middle.llvmir;

import middle.llvmir.type.IrValueType;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * LLVM IR User
 *
 */
public class IrUser extends IrValue {
    private int numOp;
    private LinkedList<IrUse> operands; // 通过Use边连接到使用的操作数

    public IrUser(IrValueType valueType) {
        super(valueType);
        this.operands = new LinkedList<>();
    }

    public IrUser(IrValueType valueType, int numOp) {
        this(valueType);
        this.numOp = numOp;
        this.operands = new LinkedList<>();
    }

    /* 向operands中添加新Use */
    public void addUse(IrUse use) {
        this.operands.add(use);
    }

    public void setOperand(IrValue value, int index) {
        for (IrUse use : operands) {
            /* 如果已经存在则替换，说明应当更换index位置的Value */
            if (use.getOperandRank() == index) {
                use.getValue().removeUse(use); // 在该use原对应的value的uses里删去本use
                use.setValue(value); // 为该use设置新value
                value.addUse(use); // 为新value设置use
                return;
            }
        }
        IrUse newUse = new IrUse(value, this, index);
        this.operands.add(newUse); // TODO
    }

    /* 获取操作数数量，可以用来处理br等操作数不定的情况 */
    public int getNumOp() {
        return numOp;
    }

    /* 根据给定index找到指定操作数 */
    public IrValue getOperand(int index) {
        for (IrUse use : this.operands) {
            if (use.getOperandRank() == index) {
                return use.getValue();
            }
        }
        /* 不应当运行到这里 */
        System.out.println("ERROR in IrUser! Should not reach here");
        return null;
    }

    @Override
    public ArrayList<String> irOutput() {
        /* TODO : 待施工 */
        return super.irOutput();
    }
}
