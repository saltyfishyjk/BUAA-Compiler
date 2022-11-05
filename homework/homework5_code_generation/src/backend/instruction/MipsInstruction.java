package backend.instruction;

import backend.MipsNode;

import java.util.ArrayList;

public abstract class MipsInstruction implements MipsNode {
    private String instName;

    public MipsInstruction(String instName) {
        this.instName = instName;
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        /* 该父类不应该被直接生成 */
        return null;
    }
}
