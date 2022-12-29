package backend.instruction;

import java.util.ArrayList;

public class Beq extends MipsInstruction {
    private int left;
    private int right;
    private String label;

    public Beq(int left, int right, String label) {
        super("beq");
        this.left = left;
        this.right = right;
        this.label = label;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getInstName() + " ");
        sb.append(RegisterName.getName(this.left) + ", ");
        sb.append(RegisterName.getName(this.right) + ", ");
        sb.append(this.label + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
