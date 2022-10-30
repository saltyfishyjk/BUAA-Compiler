package middle.llvmir.type;

import java.util.ArrayList;

/**
 * LLVM IR Array Type
 * 数组类型，通过多层嵌套来表示多维数组
 * 比如，三维数组可以是[2 x [3 x [4 x i32]]]
 */
public class IrArrayType extends IrValueType {
    private int intContains; // 总共存有多少int值（因为SysY中值类型只有int）
    private IrValueType valueType; // 该数组的元素的ValueType
    private int numElements; // 该数组元素数量

    public IrArrayType(IrValueType valueType, int numElements) {
        this.valueType = valueType; // 数组元素类型
        this.numElements = numElements; // 数组元素数量
        // 如果数组元素是int，说明当前数组为一维数组，可以直接计算int值数量
        if (this.valueType instanceof IrIntegerType) {
            this.intContains = numElements;
        } else {
            // 否则说明是多维数组，递归下降计算int值数量
            this.intContains = ((IrArrayType)valueType).getIntContains() * this.numElements;
        }
    }

    public int getIntContains() {
        return intContains;
    }

    public IrValueType getEleType() {
        return valueType;
    }

    @Override
    public ArrayList<String> irOutput() {
        /* TODO : 本次作业不涉及数组 */
        return super.irOutput();
    }
}
