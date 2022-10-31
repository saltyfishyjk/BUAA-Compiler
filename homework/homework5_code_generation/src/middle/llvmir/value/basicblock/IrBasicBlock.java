package middle.llvmir.value.basicblock;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrLabelType;
import middle.llvmir.value.function.IrFunction;
import middle.llvmir.value.instructions.IrInstruction;

import java.util.ArrayList;

public class IrBasicBlock extends IrValue {
    private String name; // 块的名字（label），可能没有
    private ArrayList<IrInstruction> instructions;
    private IrFunction function; // 父function

    public IrBasicBlock(String name) {
        super(IrLabelType.getLabelType());
        this.name = name;
        /* TODO : add instruments */
    }

    public void addIrInstruction(IrInstruction instruction) {
        this.instructions.add(instruction);
    }

    public void addAllIrInstruction(ArrayList<IrInstruction> instructions) {
        this.instructions.addAll(instructions);
    }
}
