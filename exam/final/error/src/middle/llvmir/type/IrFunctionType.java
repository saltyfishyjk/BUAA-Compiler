package middle.llvmir.type;

import middle.llvmir.IrValue;

import java.util.ArrayList;

/**
 * LLVM IR Function Type
 * 函数类型
 */
public class IrFunctionType extends IrValueType {
    private IrValueType retType; // 函数的返回类型，只有void和int两种可能
    private ArrayList<IrValueType> paramTypes; // 函数形参类型列表
    private ArrayList<IrValue> params; // 函数形参列表

    public IrFunctionType(IrValueType retType, ArrayList<IrValueType> paramTypes) {
        this.retType = retType;
        this.paramTypes = paramTypes;
    }

    public void setParams(ArrayList<IrValue> params) {
        this.params = params;
    }

    public ArrayList<IrValue> getParams() {
        return this.params;
    }

    public IrValueType getRetType() {
        return retType;
    }

    public ArrayList<IrValueType> getParamTypes() {
        return paramTypes;
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        if (this.retType instanceof IrIntegerType) {
            ret.add("i32");
        } else if (this.retType instanceof IrVoidType) {
            ret.add("void");
        }
        return ret;
    }
}
