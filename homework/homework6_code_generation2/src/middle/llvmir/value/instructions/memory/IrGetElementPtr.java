package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrArrayType;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrPointerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

import java.util.ArrayList;

/**
 * <result> = getelementptr <ty>, * {, [inrange] <ty> <idx>}*
 * <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
 */
public class IrGetElementPtr extends IrInstruction {
    private IrValueType elementType;

    public IrGetElementPtr(IrValue pointer, ArrayList<IrValue> index) {
        super(IrInstructionType.GEP,
                new IrPointerType(getElementType(pointer, index)),
                0);
        setOperand(pointer, 0);
        for (int i = 0; i < index.size(); i++) {
            setOperand(index.get(i), i + 1);
        }
        this.elementType = getElementType(pointer, index);
    }

    /* TODO */
    private static IrValueType getElementType(IrValue ptr, ArrayList<IrValue> indeices) {
        IrValueType type = ((IrPointerType)ptr.getValueType()).getContained();
        if (type instanceof IrIntegerType) {
            return type;
        } else if (type instanceof IrArrayType) {
            for (int i = 1; i < indeices.size(); i++) {
                type = ((IrArrayType) type).getEleType();
            }
            return type;
        }
        return null; // SHOULD NOT REACH HERE
    }
}
