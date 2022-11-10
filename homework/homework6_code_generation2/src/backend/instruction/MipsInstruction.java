package backend.instruction;

import backend.MipsNode;

import java.util.ArrayList;

public abstract class MipsInstruction implements MipsNode {
    private String instName;
    private String comment;

    public MipsInstruction(String instName) {
        this.instName = instName;
        this.comment = "";
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        /* 该父类不应该被直接生成 */
        return null;
    }
}
