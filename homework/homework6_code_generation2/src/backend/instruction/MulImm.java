package backend.instruction;

import java.util.ArrayList;

/**
 * 计算寄存器source * 立即数imm，并将结果保存在target中
 * mul target, source, imm
 * target : 存储答案
 * source : 被乘数寄存器
 * imm : 被乘立即数
 */
public class MulImm extends MipsInstruction {
    private int target;
    private int source;
    private int imm;

    public MulImm(int target, int source, int imm) {
        super("mul");
        this.target = target;
        this.source = source;
        this.imm = imm;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.target) + ", ");
        sb.append(RegisterName.getName(this.source) + ", ");
        sb.append(imm + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.target) + ", ");
        sb.append(RegisterName.getName(this.source) + ", ");
        sb.append(imm + "\n");
        return sb.toString();
    }
}
