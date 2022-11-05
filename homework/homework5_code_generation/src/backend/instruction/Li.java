package backend.instruction;

import java.util.ArrayList;

/**
 * Mips Li : Load Immediate 加载立即数（支持至多32位）
 * s.g. : li $8, 1000
 * $8 -> regNum
 * 1000 -> immediate
 */
public class Li extends MipsInstruction {
    private int regNum; // 寄存器编号
    private int immediate; // 立即数

    public Li(int regNum, int immediate) {
        super("li");
        this.regNum = regNum;
        this.immediate = immediate;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.regNum) + ", ");
        sb.append("0x" + Integer.toHexString(this.immediate) + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
