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

    /* TODO : 处理维度变量是IrValue的情况 */
    private IrValue dimension1RightValue = null;
    private IrValue dimension1PointerValue = null;
    private IrValue dimension2RightValue = null;
    private IrValue dimension2PointerValue = null;

    private boolean handleIrValue = false;

    /* 缺省构造器，用于处理左右都是普通常变量的情况 */
    public IrStore(IrValue value, IrValue pointer) {
        super(IrInstructionType.Store, IrVoidType.getVoidType(), 2);
        this.setOperand(value, 0);
        this.setOperand(pointer, 1);
        this.dimensionRight = 0;
        this.dimension1Pointer = 0;
    }

    public int getDimensionPointer() {
        return this.dimensionPointer;
    }

    public int getDimension1Pointer() {
        return this.dimension1Pointer;
    }

    public int getDimension2Pointer() {
        return this.dimension2Pointer;
    }

    public IrValue getDimension1PointerValue() {
        return this.dimension1PointerValue;
    }

    public IrValue getDimension2PointerValue() {
        return this.dimension2PointerValue;
    }

    public boolean getHandleIrValue() {
        return this.handleIrValue;
    }

    /* 处理涉及数组的赋值 */
    /* 需要传入左右的维数和维度变量 */
    /* 在处理数组初始化的时候由于可以直接知道常数的维度变量 */
    /* 因此在这里用int处理 */
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
        this.handleIrValue = false;
    }

    /* 处理涉及数组的赋值 */
    /* 针对StmtGetInt和StmtAssign中左值为数组时的情况 */
    /* 数组维数变量是Exp，因此传入IrValue */
    public IrStore(IrValue value,
                   IrValue pointer,
                   int dimensionRight,
                   int dimensionPointer,
                   IrValue dimension1RightValue,
                   IrValue dimension1PointerValue,
                   IrValue dimension2RightValue,
                   IrValue dimension2PointerValue) {
        super(IrInstructionType.Store, IrVoidType.getVoidType(), 2);
        this.setOperand(value, 0);
        this.setOperand(pointer, 1);
        this.dimensionRight = dimensionRight;
        this.dimensionPointer = dimensionPointer;
        this.dimension1RightValue = dimension1RightValue;
        this.dimension1PointerValue = dimension1PointerValue;
        this.dimension2RightValue = dimension2RightValue;
        this.dimension2PointerValue = dimension2PointerValue;
        this.handleIrValue = true;
    }

    @Override
    public ArrayList<String> irOutput() {
        StringBuilder sb = new StringBuilder();
        if (!handleIrValue) {
            if (this.dimensionRight == 0 && this.dimensionPointer == 0) {
                /* 缺省情况，左右均是普通常变量 */
                sb.append("store i32 "); // 这里应当只有i32一种存储
                IrValue operand1 = this.getOperand(0);
                IrValue operand2 = this.getOperand(1);
                sb.append(operand1.getName() + ", i32* ");
                sb.append(operand2.getName() + "\n");
            } else {
                sb.append("store i32 ");
                /* 被存的值 */
                IrValue operand1 = this.getOperand(0);
                /* 存入的目标 */

                sb.append(operand1.getName());
                if (this.dimensionRight == 0) {
                    /* 普通变量不需要做什么 */
                } else if (this.dimensionRight == 1) {
                    /* 1维数组变量 */
                    sb.append("[" + this.dimension1Right + "]");
                } else if (this.dimensionRight == 2) {
                    /* 2维数组变量 */
                    sb.append("[" + this.dimension1Right + "][" + this.dimension2Right + "]");
                } else {
                    System.out.println("ERROR IN IrStore : should not reach here");
                }
                sb.append(", i32* ");
                IrValue operand2 = this.getOperand(1);
                sb.append(operand2.getName());
                if (this.dimensionPointer == 0) {
                    /* 普通变量不需要做什么 */
                } else if (this.dimensionPointer == 1) {
                    /* 1维数组变量 */
                    sb.append("[" + this.dimension1Pointer + "]");
                } else if (this.dimensionPointer == 2) {
                    /* 2维数组变量 */
                    sb.append("[" + this.dimension1Pointer + "][" + this.dimension2Pointer + "]");
                }
                sb.append("\n");
            }
        } else {
            sb.append("store i32 ");
            /* 被存的值 */
            IrValue operand1 = this.getOperand(0);
            /* 存入的目标 */
            sb.append(operand1.getName());
            if (this.dimensionRight == 0) {
                /* 普通变量不需要做什么 */
            } else if (this.dimensionRight == 1) {
                /* 1维数组变量 */
                sb.append("[" + this.dimension1RightValue.getName() + "]");
            } else if (this.dimensionRight == 2) {
                /* 2维数组变量 */
                sb.append("[" + this.dimension1RightValue.getName() + "]");
                sb.append("[" + this.dimension2RightValue.getName() + "]");
            } else {
                System.out.println("ERROR IN IrStore : should not reach here");
            }
            sb.append(", i32* ");
            IrValue operand2 = this.getOperand(1);
            sb.append(operand2.getName());
            if (this.dimensionPointer == 0) {
                /* 普通变量不需要做什么 */
            } else if (this.dimensionPointer == 1) {
                /* 1维数组变量 */
                sb.append("[" + this.dimension1PointerValue.getName() + "]");
            } else if (this.dimensionPointer == 2) {
                /* 2维数组变量 */
                sb.append("[" + this.dimension1PointerValue.getName() + "]");
                sb.append("[" + this.dimension2PointerValue.getName() + "]");
            } else {
                System.out.println("ERROR IN IrStore : should not reach here");
            }
            sb.append("\n");
        }
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }
}
