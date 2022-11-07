package backend.symbol;

/**
 * Mips Symbol : Mips符号表中的符号
 * 初始阶段，采用FIFO的方法进行寄存器分配，后续可以使用其他优化算法
 * 由于在中间代码中已经消除了变量名冲突，因此在这里直接将中间代码的变量名和寄存器/内存映射起来
 *
 */
public class MipsSymbol {
    private String name; // LLVM IR中的符号名
    private boolean inReg; // 标记当前变量是否在寄存器中
    private int regIndex; // 当inReg时，标记当前变量所在寄存器位置
    private boolean dirty; // 标记当需要释放该符号所对应的寄存器时，是否需要回写内存
    private boolean hasRam; // 标记当前符号是否拥有合法的offset
    private int base; // gp=28, fp=30 标记当前符号在内存中的基地址所在的寄存器，具体地，全局变量是gp，局部变量是fp
    private int offset; // 标记当前符号在内存中相对于$base的偏移
    private boolean isTemp; // 标记当前符号是否是临时变量
    private boolean used; // 若本符号为临时变量，标记是否被使用过。由于LLVM IR是SSA，因此一旦被使用就可以free，且不用写回内存

    /* 为IrAlloca生成符号 */
    public MipsSymbol(String name, int base) {
        this.name = name;
        this.inReg = false;
        this.regIndex = -1;
        this.base = base;
        this.hasRam = false;
        this.offset = 0;
        this.isTemp = false;
        this.used = false;
    }

    /* 当寄存器充足时，为该符号设置inReg = true, regIndex为对应的寄存器数字编号 */
    public MipsSymbol(String name, int base, boolean inReg, int regIndex) {
        this(name, base);
        this.inReg = inReg;
        this.regIndex = regIndex;
    }

    /* 为全局变量生成符号 */
    public MipsSymbol(String name, int base, int offset) {
        this(name, base);
        this.inReg = false;
        this.regIndex = -1;
        this.offset = offset;
        this.dirty = true;
        this.hasRam = true;
        this.isTemp = false;
    }

    /* 为$a寄存器中的传入参数生成符号 */
    public MipsSymbol(String name,
                      int base,
                      boolean inReg,
                      int regIndex,
                      boolean isTemp) {
        this.name = name;
        this.base = base;
        this.inReg = inReg;
        this.regIndex = regIndex;
        this.isTemp = isTemp; // 应当为false
        this.hasRam = false;
        this.used = false;
        this.dirty = true; // 需要写回内存
    }

    /* 为通过内存fp压栈的传入参数生成符号 */
    public MipsSymbol(String name,
                      int base,
                      boolean inReg,
                      boolean hasRam,
                      int offset,
                      boolean isTemp) {
        this.name = name;
        this.base = base;
        this.inReg = inReg;
        this.hasRam = hasRam;
        this.offset = offset;
        this.isTemp = isTemp;
    }

    /* 通用构造器 */
    public MipsSymbol(String name,
                      int base,
                      boolean inReg,
                      int regIndex,
                      boolean hasRam,
                      int offset,
                      boolean isTemp,
                      boolean used) {
        this.name = name;
        this.base = base;
        this.inReg = inReg;
        this.regIndex = regIndex;
        this.hasRam = hasRam;
        this.offset = offset;
        this.isTemp = isTemp;
        this.used = used;
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

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setInReg(boolean inReg) {
        this.inReg = inReg;
    }

    public void setRegIndex(int regIndex) {
        this.regIndex = regIndex;
    }

    public boolean hasRam() {
        return hasRam;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setHasRam(boolean hasRam) {
        this.hasRam = hasRam;
    }

    public int getOffset() {
        return offset;
    }

    public int getBase() {
        return base;
    }

    public String getName() {
        return name;
    }
}
