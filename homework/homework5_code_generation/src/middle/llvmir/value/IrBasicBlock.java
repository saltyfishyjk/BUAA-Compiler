package middle.llvmir.value;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrLabelType;

public class IrBasicBlock extends IrValue {
    private String name;

    public IrBasicBlock(String name) {
        super(IrLabelType.getLabelType());
        /* TODO : add instruments */
    }
}
