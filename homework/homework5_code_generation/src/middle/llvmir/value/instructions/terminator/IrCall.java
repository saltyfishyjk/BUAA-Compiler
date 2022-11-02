package middle.llvmir.value.instructions.terminator;

import middle.llvmir.IrValue;
import middle.llvmir.type.IrFunctionType;
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
        this.setName(function.getName());
    }

    public IrFunction getFunction() {
        return (IrFunction)this.getOperand(0);
    }

    @Override
    public ArrayList<String> irOutput() {

        StringBuilder sb = new StringBuilder();
        sb.append("call ");
        if (retVoid) {
            sb.append("void ");
        } else {
            sb.append("i32 ");
        }
        sb.append(this.getName());
        sb.append("(");
        if (this.getNumOp() > 1) {
            // 有参数
            int len = this.getNumOp();
            for (int i = 1; i < len; i++) {
                ArrayList<String> arg = this.getOperand(i).irOutput();
                if (arg == null || arg.size() != 1) {
                    System.out.println("ERROR in IrCall : should not reach here");
                }
                sb.append(arg.get(0));
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
}
