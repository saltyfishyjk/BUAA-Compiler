package backend.instruction;

import java.util.ArrayList;

/**
 * Mips Label : 标签
 * s.g. : label:
 */
public class Label extends MipsInstruction {
    private String labelName;

    public Label(String labelName) {
        super(labelName);
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + ":\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
