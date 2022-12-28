package backend.instruction;

import java.util.ArrayList;

/**
 * Mips lw : Load Word 加载字
 * R[rt] = Mem[GPR[rs] + sign_ext(offset)]
 */
public class Lw extends MipsInstruction {
    private int rt; // 目标寄存器编号
    private int base; // 存放base地址的寄存器编号
    private int offset; // 偏移

    public Lw(int rt, int base, int offset) {
        super("lw");
        this.rt = rt;
        this.base = base;
        this.offset = offset;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder lw = new StringBuilder();
        /* 指令签名 */
        lw.append(this.getInstName() + " ");
        /* 操作体 */
        lw.append(RegisterName.getName(rt) + ", ");
        lw.append(this.offset + "(" + RegisterName.getName(base) + ")\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(lw.toString());
        return ret;
    }
    
    @Override
    public String toString() {
        StringBuilder lw = new StringBuilder();
        /* 指令签名 */
        lw.append(this.getInstName() + " ");
        /* 操作体 */
        lw.append(RegisterName.getName(rt) + ", ");
        lw.append(this.offset + "(" + RegisterName.getName(base) + ")\n");
        return lw.toString();
    }
}
