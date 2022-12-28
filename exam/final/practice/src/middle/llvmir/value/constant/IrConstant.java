package middle.llvmir.value.constant;

import middle.llvmir.IrUser;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.IrNode;

import java.util.ArrayList;

public class IrConstant extends IrUser implements IrNode {
    public IrConstant(IrValueType type) {
        super(type);
    }

    public IrConstant(IrValueType type, int numOp) {
        super(type, numOp);
    }

    @Override
    public ArrayList<String> irOutput() {
        return new ArrayList<>();
    }
}
