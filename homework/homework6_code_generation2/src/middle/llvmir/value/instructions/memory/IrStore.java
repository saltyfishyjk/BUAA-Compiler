package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrVoidType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

import java.util.ArrayList;

/**
 * store : store <ty> <value>, <ty>* <pointer>
 * value : 被存的值
 * pointer : 存向的目标
 * LLVM IR Store 内存存储指令
 */
public class IrStore extends IrInstruction {
    private int dimensionRight = 0;
    private int dimensionPointer = 0;
    private int dimension1Right = 0;
    private int dimension1Pointer = 0;
    private int dimension2Right = 0;
    private int dimension2Pointer = 0;

    /* 缺省构造器，用于处理左右都是普通常变量的情况 */
    public IrStore(IrValue value, IrValue pointer) {
        super(IrInstructionType.Store, IrVoidType.getVoidType(), 2);
        this.setOperand(value, 0);
        this.setOperand(pointer, 1);
        this.dimensionRight = 0;
        this.dimension1Pointer = 0;
    }

    /* 处理涉及数组的赋值 */
    /* 需要传入左右的维数和维度变量 */
    public IrStore(IrValue value,
                   IrValue pointer,
                   int dimensionRight,
                   int dimensionPointer,
                   int dimension1Right,
                   int dimension1Pointer,
                   int dimension2Right,
                   int dimension2Pointer) {
        super(IrInstructionType.Store, IrVoidType.getVoidType(), 2);
        this.setOperand(value, 0);
        this.setOperand(pointer, 1);
        this.dimensionRight = dimensionRight;
        this.dimensionPointer = dimensionPointer;
        this.dimension1Right = dimension1Right;
        this.dimension1Pointer = dimension1Pointer;
        this.dimension2Right = dimension2Right;
        this.dimension2Pointer = dimension2Pointer;
    }

    @Override
    public ArrayList<String> irOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append("store i32 "); // TODO : 这里应当只有i32一种存储
        IrValue operand1 = this.getOperand(0);
        IrValue operand2 = this.getOperand(1);
        sb.append(operand1.getName() + ", i32* ");
        sb.append(operand2.getName() + "\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
