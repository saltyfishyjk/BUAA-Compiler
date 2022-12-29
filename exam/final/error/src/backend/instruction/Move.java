package backend.instruction;

import java.util.ArrayList;

/**
 * Mips Move : 搬运
 * move $t0, $t1 将$t1的值赋给$t0 move $t0 from $t1
 *
 */
public class Move extends MipsInstruction {
    private int target;
    private int source;

    public Move(int target, int source) {
        super("move");
        this.target = target;
        this.source = source;
    }

    public Move(int target, int source, String comment) {
        this(target, source);
        this.setComment(comment);
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getComment());
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.target) + ", ");
        sb.append(RegisterName.getName(this.source) + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getComment());
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.target) + ", ");
        sb.append(RegisterName.getName(this.source) + "\n");
        return sb.toString();
    }
}
