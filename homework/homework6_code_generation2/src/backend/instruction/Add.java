package backend.instruction;

import java.util.ArrayList;

public class Add extends MipsInstruction {
    private int target;
    private int left;
    private int right;

    public Add(int target, int left, int right) {
        super("addu");
        this.target = target;
        this.left = left;
        this.right = right;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.target) + ", ");
        sb.append(RegisterName.getName(this.left) + ", ");
        sb.append(RegisterName.getName(this.right) + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.target) + ", ");
        sb.append(RegisterName.getName(this.left) + ", ");
        sb.append(RegisterName.getName(this.right) + "\n");
        return sb.toString();
    }
}
