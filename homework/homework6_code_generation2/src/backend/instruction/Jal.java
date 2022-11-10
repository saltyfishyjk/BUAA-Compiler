package backend.instruction;

import java.util.ArrayList;

public class Jal extends MipsInstruction {
    private String target;

    public Jal(String target) {
        super("jal");
        this.target = target;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(this.target + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
