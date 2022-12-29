package backend.instruction;

import java.util.ArrayList;

public class Nop extends MipsInstruction {
    public Nop() {
        super("nop");
    }

    @Override
    public ArrayList<String> mipsOutput() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(this.getInstName() + "\n");
        return ret;
    }
}
