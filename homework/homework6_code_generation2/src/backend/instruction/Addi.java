package backend.instruction;

import java.util.ArrayList;

public class Addi extends MipsInstruction {
    private int target;
    private int source;
    private int immediate;

    public Addi(int target, int source, int immediate) {
        super("addiu");
        this.target = target;
        this.source = source;
        this.immediate = immediate;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(target) + ", ");
        sb.append(RegisterName.getName(source) + ", ");
        sb.append(immediate + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(target) + ", ");
        sb.append(RegisterName.getName(source) + ", ");
        sb.append(immediate + "\n");
        return sb.toString();
    }
}
