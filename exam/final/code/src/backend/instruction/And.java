package backend.instruction;

import java.util.ArrayList;

public class And extends MipsInstruction {
    private int target;
    private int left;
    private int right;
    
    public And(int target, int left, int right) {
        super("and");
        this.target = target;
        this.left = left;
        this.right = right;
    }

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
}
