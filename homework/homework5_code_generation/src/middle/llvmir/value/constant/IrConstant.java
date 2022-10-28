package middle.llvmir.value.constant;

import middle.llvmir.IrUser;
import middle.llvmir.type.IrValueType;

public class IrConstant extends IrUser {
    public IrConstant(IrValueType type) {
        super(type);
    }

    public IrConstant(IrValueType type, int numOp) {
        super(type, numOp);
    }
}
