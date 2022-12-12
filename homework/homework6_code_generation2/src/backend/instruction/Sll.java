package backend.instruction;

import java.util.ArrayList;

public class Sll extends MipsInstruction {
    private int target;
    private int source;
    private int offset;
    
    public Sll(int target, int source, int offset) {
        super("sll");
        this.target = target;
        this.source = source;
        this.offset = offset;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.target) + ", ");
        sb.append(RegisterName.getName(this.source) + ", ");
        sb.append(offset + "\n");
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
        sb.append(offset + "\n");
        return sb.toString();
    }
}
