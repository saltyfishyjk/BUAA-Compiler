package backend.instruction;

import java.util.ArrayList;

/**
 * Mips jr : Jump Register 跳转到指定寄存器保存的label
 */
public class Jr extends MipsInstruction {
    private int regNum; // 跳转的寄存器编号

    public Jr(int regNum) {
        super("jr");
        this.regNum = regNum;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(regNum) + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
