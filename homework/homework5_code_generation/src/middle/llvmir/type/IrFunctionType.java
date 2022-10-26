package middle.llvmir.type;

import java.util.ArrayList;

public class IrFunctionType extends IrValueType {
    private IrValueType retType; // 函数的返回类型，只有void和int两种可能
    private ArrayList<IrValueType> paramTypes; // 函数形参类型列表

    public IrFunctionType(IrValueType retType, ArrayList<IrValueType> paramTypes) {
        this.retType = retType;
        this.paramTypes = paramTypes;
    }

    public IrValueType getRetType() {
        return retType;
    }

    public ArrayList<IrValueType> getParamTypes() {
        return paramTypes;
    }
}
