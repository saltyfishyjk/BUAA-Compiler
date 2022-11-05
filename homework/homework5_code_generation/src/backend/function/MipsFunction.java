package backend.function;

import backend.MipsBasicBlock;
import backend.MipsNode;

import java.util.ArrayList;

/**
 * Mips Function
 * Mips 函数
 */
public class MipsFunction implements MipsNode {
    private String name; // 函数标签名
    private boolean isMain; // 标记是否是main函数：main函数的部分处理和其他函数不同
    private ArrayList<MipsBasicBlock> mipsBasicBlocks;

    public MipsFunction() {
        this.mipsBasicBlocks = new ArrayList<>();
    }

    public MipsFunction(boolean isMain) {
        this();
        this.isMain = isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        ArrayList<String> ret =  new ArrayList<>();
        /* 打印函数签名 */
        ret.add(this.name + ":\n");
        /* 打印MipsBasicBlock */
        for (MipsBasicBlock block : this.mipsBasicBlocks) {
            ArrayList<String> temp = block.mipsOutput();
            if (temp != null && temp.size() > 0) {
                for (String index : temp) {
                    ret.add(index);
                }
            }
        }
        /* 当非main函数的时候，需要打印跳回指令 */
        return ret;
    }
}
