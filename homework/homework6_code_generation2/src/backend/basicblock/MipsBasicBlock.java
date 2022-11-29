package backend.basicblock;

import backend.MipsNode;
import backend.function.MipsFunction;
import backend.instruction.MipsInstruction;
import backend.symbol.MipsSymbolTable;

import java.util.ArrayList;

public class MipsBasicBlock implements MipsNode {
    private MipsFunction father; // 父MipsFunction
    private ArrayList<MipsInstruction> instructions;
    private MipsSymbolTable table;

    public MipsBasicBlock(MipsFunction father) {
        this.father = father;
        this.instructions = new ArrayList<>();
        this.table = this.father.getTable();
    }

    @Override
    public ArrayList<String> mipsOutput() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add("\n# New Basic Block\n");
        ArrayList<String> temp;
        for (MipsInstruction instruction : instructions) {
            if (instruction == null) {
                continue;
            }
            temp = instruction.mipsOutput();
            if (temp != null && temp.size() > 0) {
                for (String index : temp) {
                    ret.add(index);
                }
            }
        }
        return ret;
    }

    public void addInstruction(ArrayList<MipsInstruction> instructions) {
        if (instructions != null && instructions.size() > 0) {
            for (MipsInstruction instruction : instructions) {
                this.instructions.add(instruction);
            }
        }
    }

    public MipsSymbolTable getTable() {
        return table;
    }

    public MipsFunction getFather() {
        return this.father;
    }
}
