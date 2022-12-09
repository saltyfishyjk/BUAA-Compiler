package middle.llvmir.value.instructions.terminator;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrFunctionType;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrVoidType;
import middle.llvmir.value.function.IrFunction;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;

import java.util.ArrayList;

/**
 * Call -> <result> = call [ret attrs] <ty> <fnptrval>(<function args>)
 * LLVM IR Call 函数调用指令
 * ty : type Value类型
 * fnptrval : FunctionPointerValue 为一个要调用函数的指针
 * function args : 函数参数，第0个参数是函数名
 */
public class IrCall extends IrInstruction {
    private boolean retVoid; // 返回值是否为void类型
    private String functionName;

    public IrCall(IrFunction function, ArrayList<IrValue> args) {
        super(IrInstructionType.Call,
                ((IrFunctionType)function.getValueType()).getRetType(),
                args.size() + 1); // 函数名是第0个参数，因此+1
        if (this.getValueType() instanceof IrVoidType) {
            this.retVoid = true;
        } else {
            this.retVoid = false;
        }
        this.setOperand(function, 0); // 函数名是第一个参数
        int len = args.size();
        for (int i = 0; i < len; i++) {
            this.setOperand(args.get(i), i + 1); // 由于函数名置于0位，后续操作数依次后延
        }
        this.functionName = function.getName();
        // this.setName(function.getName());
    }

    /* 处理getint */
    public IrCall(String functionName) {
        super(IrInstructionType.Call, IrIntegerType.get32(), 0);
        this.functionName = functionName;
        this.retVoid = false;
    }

    /* 处理putch(i32)*/
    public IrCall(String functionName, char c) {
        super(IrInstructionType.Call, IrVoidType.getVoidType(), 2);
        this.functionName = functionName;
        this.retVoid = true;
        // IrValue value = new IrValue(IrIntegerType.get32(), "i32 " + String.valueOf((int)c));
        IrValue value = new IrValue(IrIntegerType.get32(), String.valueOf((int)c));
        this.setOperand(value, 1);
    }

    /* 处理putint(i32)*/
    public IrCall(String functionName, IrValue value) {
        super(IrInstructionType.Call, IrVoidType.getVoidType(), 2);
        this.functionName = functionName;
        this.retVoid = true;
        //  value = new IrValue(IrIntegerType.get32(), String.valueOf(num));
        // value.setName("i32 " + value.getName());
        this.setOperand(value, 1);
    }

    public IrFunction getFunction() {
        return (IrFunction)this.getOperand(0);
    }

    @Override
    public ArrayList<String> irOutput() {
        StringBuilder sb = new StringBuilder();
        if (!this.retVoid) {
            sb.append(this.getName() + " = ");
        }
        sb.append("call ");
        if (retVoid) {
            sb.append("void ");
        } else {
            sb.append("i32 ");
        }
        sb.append(this.functionName);
        sb.append("(");
        if (this.getNumOp() > 1) {
            // 有参数
            int len = this.getNumOp();
            for (int i = 1; i < len; i++) {
                IrValue arg = this.getOperand(i);
                if (arg.getDimensionValue() == -1) {
                    sb.append("i32 "); // TODO :此处默认是i32，后续需要处理数组
                    sb.append(arg.getName());
                } else if (arg.getDimensionValue() == 0) {
                    /* 要传入一个0维参数 */
                    sb.append("i32 ");
                    sb.append(arg.getName());
                    /* arg符号本身的维数不可能是0，因为是0不会走到setDimensionValue */
                    if (arg.getDimension() == 1) {
                        /* 形如a[1] */
                        IrValue dimension1Value = arg.getDimension1Value();
                        sb.append("[" + dimension1Value.getName() + "] ");
                    } else if (arg.getDimension() == 2) {
                        /* 形如a[1][2] */
                        IrValue dimension1Value = arg.getDimension1Value();
                        sb.append("[" + dimension1Value.getName() + "]");
                        IrValue dimension2Value = arg.getDimension2Value();
                        sb.append("[" + dimension2Value.getName() + "] ");
                    } else {
                        System.out.println("ERROR in IrCall : should not reach here");
                    }
                } else if (arg.getDimensionValue() == 1) {
                    sb.append("i32* ");
                    sb.append(arg.getName());
                    /* 要传入一个1维参数 */
                    if (arg.getDimension() == 1) {
                        /* 形如a */
                        /* 无需再append什么内容 */
                    } else if (arg.getDimension() == 2) {
                        /* 形如a[1] */
                        IrValue dimension1Value = arg.getDimension1Value();
                        sb.append("[" + dimension1Value.getName() + "] ");
                    }
                } else if (arg.getDimensionValue() == 2) {
                    sb.append("i32** ");
                    /* 传入二维数组，形如a */
                    sb.append(arg.getName());
                }
                if (i != len - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append(")\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(sb.toString());
        return ret;
    }

    public boolean getVoid() {
        return this.retVoid;
    }

    public String getFunctionName() {
        return functionName;
    }

    public ArrayList<IrValue> getParams() {
        int len = this.getNumOp();
        ArrayList<IrValue> ret = new ArrayList<>();
        for (int i = 1; i < len; i++) {
            ret.add(this.getOperand(i));
        }
        return  ret;
    }
}
