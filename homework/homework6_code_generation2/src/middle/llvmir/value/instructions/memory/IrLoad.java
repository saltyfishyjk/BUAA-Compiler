package middle.llvmir.value.instructions.memory;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

import java.util.ArrayList;

/**
 * load : <result> = load <ty>, <ty>* <pointer>
 * LLVM IR Load 内存读取指令
 * result : 内存读取后储存变量
 * ty : 变量类型，在本实验中只有i32
 * pointer :
 */
public class IrLoad extends IrInstruction {
    private int dimension = 0;
    private IrValue dimension1 = null;
    private IrValue dimension2 = null;
    private boolean array = false;

    /* 缺省构造器，用于处理加载普通变量的情况 */
    public IrLoad(IrValueType valueType, IrValue value) {
        super(IrInstructionType.Load, valueType, 1);
        this.setOperand(value, 0);
        this.array = false;
    }

    /* 处理涉及数组的赋值，需要传入数组的维数和维度值 */
    /* 由于涉及IrLoad的只有LVal，由文法知道其维度变量是Exp */
    /* 在我们的编译器中会将其解析为IrValue */
    public IrLoad(IrValueType valueType, IrValue value, int dimension,
                  IrValue dimension1, IrValue dimension2) {
        super(IrInstructionType.Load, valueType, 1);
        this.setOperand(value, 0);
        this.dimension = dimension;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.array = true;
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (!this.array) {
            /* 加载的元素不是数组 */
            sb.append(this.getName() + " = load i32, i32* " + this.getOperand(0).getName() + "\n");
        } else {
            /* 加载的元素是数组 */
            sb.append(this.getName() + " = load i32, i32* ");
            sb.append(this.getOperand(0).getName());
            sb.append("[" + this.dimension1.getName() + "]");
            if (this.dimension2 != null) {
                sb.append("[" + this.dimension2.getName() + "]");
            }
            sb.append("\n");
        }
        ret.add(sb.toString());
        return ret;
    }
    
    public IrValue getDimension1Value() {
        return this.dimension1;
    }

    public IrValue getDimension2Value() {
        return this.dimension2;
    }
}
