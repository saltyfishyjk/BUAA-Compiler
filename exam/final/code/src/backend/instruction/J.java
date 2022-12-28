package backend.instruction;

import java.util.ArrayList;

/**
 * Mips J : Jump 跳转
 *
 */
public class J extends MipsInstruction {
    private String label; // 跳转目标地址

    public J(String label) {
        super("j");
        this.label = label;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        /* 输出j label */
        sb.append(this.getInstName() + " ");
        sb.append(this.label + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
