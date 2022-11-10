package backend.instruction;

import java.util.ArrayList;

public class Syscall extends MipsInstruction {
    public Syscall() {
        super("syscall");
    }

    @Override
    public ArrayList<String> mipsOutput() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(this.getInstName() + "\n");
        return ret;
    }
}
