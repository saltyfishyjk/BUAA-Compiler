package backend.function;

import backend.basicblock.MipsBasicBlock;
import backend.MipsModule;
import backend.MipsNode;
import backend.instruction.Jr;
import backend.instruction.Li;
import backend.instruction.Syscall;
import backend.symbol.MipsSymbolTable;

import java.util.ArrayList;

/**
 * Mips Function
 * Mips 函数
 */
public class MipsFunction implements MipsNode {
    private String name; // 函数标签名
    private boolean isMain; // 标记是否是main函数：main函数的部分处理和其他函数不同
    private ArrayList<MipsBasicBlock> mipsBasicBlocks;
    private MipsModule father; // 父MipsModule
    private MipsSymbolTable table;

    public MipsFunction(MipsModule father) {
        this.father = father;
        this.mipsBasicBlocks = new ArrayList<>();
    }

    public MipsFunction(MipsModule father, boolean isMain, String name, MipsSymbolTable table) {
        this(father);
        this.isMain = isMain;
        this.name = name;
        this.table = table;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public void addMipsBasicBlock(MipsBasicBlock basicBlock) {
        this.mipsBasicBlocks.add(basicBlock);
    }

    @Override
    public ArrayList<String> mipsOutput() {
        ArrayList<String> ret =  new ArrayList<>();
        /* 打印函数签名注释 */
        ret.add("# ---------- " + this.name + "函数开始 ----------\n");
        /* 打印函数签名 */
        ret.add(this.name + ":\n");
        /* 打印MipsBasicBlock */
        ArrayList<String> temp;
        for (MipsBasicBlock block : this.mipsBasicBlocks) {
            temp = block.mipsOutput();
            if (temp != null && temp.size() > 0) {
                for (String index : temp) {
                    ret.add(index);
                }
            }
            ret.add("\n");
        }
        /* 当非main函数的时候，需要打印跳回指令 */
        if (!this.isMain) {
            /* $31 = $ra */
            Jr jr = new Jr(31);
            temp = jr.mipsOutput();
            for (String index : temp) {
                ret.add(index);
            }
        } else {
            /* 当main函数时，需要结束程序 */
            /* 将10装入$v0即$2 */
            Li li = new Li(2, 10);
            temp = li.mipsOutput();
            for (String index : temp) {
                ret.add(index);
            }
            /* 系统调用 */
            Syscall syscall = new Syscall();
            temp = syscall.mipsOutput();
            for (String index : temp) {
                ret.add(index);
            }
        }
        ret.add("# ********** " + this.name + "函数结束 **********\n");
        return ret;
    }

    public MipsSymbolTable getTable() {
        return table;
    }

    public MipsModule getFather() {
        return father;
    }
}
