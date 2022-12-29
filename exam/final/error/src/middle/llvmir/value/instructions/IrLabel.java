package middle.llvmir.value.instructions;

import middle.llvmir.type.IrLabelType;

import java.util.ArrayList;

/**
 * LrLabel
 */
public class IrLabel extends IrInstruction {
    public IrLabel(String name) {
        super(IrInstructionType.Label, IrLabelType.getLabelType(), 0);
        super.setName(name);
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + " : \n");
        ret.add(sb.toString());
        return ret;
    }
}
