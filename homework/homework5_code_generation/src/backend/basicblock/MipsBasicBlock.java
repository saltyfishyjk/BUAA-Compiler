package backend.basicblock;

import backend.MipsNode;
import backend.function.MipsFunction;
import backend.instruction.MipsInstruction;

import java.util.ArrayList;

public class MipsBasicBlock implements MipsNode {
    private MipsFunction father; // 父MipsFunction
    private ArrayList<MipsInstruction> instructions;

    public MipsBasicBlock(MipsFunction father) {
        this.father = father;
        this.instructions = new ArrayList<>();
    }

    @Override
    public ArrayList<String> mipsOutput() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add("\n# New Basic Block\n");
        ArrayList<String> temp;
        for (MipsInstruction instruction : instructions) {
            temp = instruction.mipsOutput();
            if (temp != null && temp.size() > 0) {
                for (String index : temp) {
                    ret.add(index);
                }
            }
        }
        return ret;
    }

    public void addInstruction(MipsInstruction instruction) {
        this.instructions.add(instruction);
    }
}
