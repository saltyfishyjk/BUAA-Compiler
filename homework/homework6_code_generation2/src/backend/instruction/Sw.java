package backend.instruction;

import java.util.ArrayList;

/**
 * Mips sw : Store Word 存储字
 * e.g. : sw $v1, 8($s0)
 * $v1 : rt 要被存储的值所在的寄存器
 * $s0 : base 内存地址基值所在的寄存器
 * 8 : offset 目的内存地址相对于base的偏移
 */
public class Sw extends MipsInstruction {
    private int rt;
    private int base;
    private int offset;

    public Sw(int rt, int base, int offset) {
        super("sw");
        this.rt = rt;
        this.base = base;
        this.offset = offset;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(rt) + ", ");
        sb.append(offset);
        sb.append("(" + RegisterName.getName(base) + ")\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
