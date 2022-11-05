package backend;

/**
 * Mips Symbol : Mips符号表中的符号
 * 初始阶段，采用FIFO的方法进行寄存器分配，后续可以使用其他优化算法
 * 由于在中间代码中已经消除了变量名冲突，因此在这里直接将中间代码的变量名和寄存器/内存映射起来
 *
 */
public class MipsSymbol {
    // private RegisterFile registerFile; // 寄存器表，表明在当前函数体中各个寄存器的使用情况
    private String name; // LLVM IR中的符号名
    private boolean inReg; // 标记当前变量是否在寄存器中
    private int regIndex; // 当inReg时，标记当前变量所在寄存器位置
    private boolean dirty; // 标记当需要释放该符号所对应的寄存器时，是否需要回写内存
    private boolean hasRam; // 标记当前符号是否拥有合法的offset
    private int offset; // 标记当前符号在内存中相对于$fp的偏移
    private boolean isTemp; // 标记当前符号是否是临时变量
    private boolean used; // 若本符号为临时变量，标记是否被使用过。由于LLVM IR是SSA，因此一旦被使用就可以free，且不用写回内存

    /* 当寄存器充足时，为该符号设置inReg = true, regIndex为对应的寄存器数字编号 */
    public MipsSymbol(boolean inReg, int regIndex) {
        this.inReg = inReg;
        this.regIndex = regIndex;
    }

    public boolean isInReg() {
        return this.inReg;
    }

    public int getRegIndex() {
        return this.regIndex;
    }

    public boolean isTemp() {
        return this.isTemp;
    }

    public boolean isUsed() {
        return this.used;
    }
}
