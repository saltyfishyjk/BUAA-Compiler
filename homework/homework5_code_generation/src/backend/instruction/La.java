package backend.instruction;

import java.util.ArrayList;

public class La extends MipsInstruction {
    private int reg;
    private String label;

    public La(int reg, String label) {
        super("la");
        this.reg = reg;
        this.label = label;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(reg) + ", ");
        sb.append(label + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
