package backend.instruction;

import java.util.ArrayList;

public class Mfhi extends MipsInstruction {
    private int target;

    public Mfhi(int target) {
        super("mfhi");
        this.target = target;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.target) + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
