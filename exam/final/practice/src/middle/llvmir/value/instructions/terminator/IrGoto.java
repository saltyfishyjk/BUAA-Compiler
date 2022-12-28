package middle.llvmir.value.instructions.terminator;

import middle.llvmir.type.IrLabelType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;
import middle.llvmir.value.instructions.IrLabel;

import java.util.ArrayList;

/**
 * Goto -> goto label
 * 无条件跳转到标签label
 */
public class IrGoto extends IrInstruction {
    public IrGoto(IrLabel label) {
        super(IrInstructionType.Goto, IrLabelType.getLabelType(), 1);
        setOperand(label, 0);
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("goto " + this.getOperand(0).getName() + "\n");
        ret.add(sb.toString());
        return ret;
    }
}
