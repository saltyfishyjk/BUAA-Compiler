package middle.llvmir.value.globalvariable;

import middle.llvmir.IrUser;
import middle.llvmir.type.IrArrayType;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrPointerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.IrNode;
import middle.llvmir.value.constant.IrConstant;

import java.util.ArrayList;

/**
 * LLVM 全局变量
 */
public class IrGlobalVariable extends IrUser implements IrNode {
    private boolean isConst; // 标记是否是常量
    private IrConstant init; // 初始化的值
    private IrValueType containedType;

    public IrGlobalVariable(String name, IrValueType type) {
        super(new IrPointerType(type));
        this.containedType = type;
        this.setName(name);
    }

    public IrGlobalVariable(String name,
                            IrValueType type,
                            boolean isConst,
                            IrConstant init) {
        this(name, type);
        this.isConst = isConst;
        this.init = init;
    }

    public void setInit(IrConstant init) {
        this.init = init;
    }

    public IrConstant getInit() {
        return init;
    }

    /**
     * 重写toString以方便调试和测试
     */
    @Override
    public String toString() {
        return "isConst:" + this.isConst + "\nname:" + this.getName();
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        if (this.isConst) {
            // 全局常量，一定被赋初值
            if (this.containedType instanceof IrIntegerType) {
                // 全局数值常量
                String temp = this.getName() + " = dso_local global "
                        + this.containedType.irOutput().get(0)
                        + " " + this.init.irOutput().get(0) + "\n";
                ret.add(temp);
            } else if (this.getValueType() instanceof IrArrayType) {
                // 全局数组常量
                /* TODO : 本次作业不涉及数组 */
                ret = ret;
            } else {
                System.out.println("ERROR in IrGlobalVariable : should not reach here");
            }
        } else {
            // 全局变量，可能被赋初值，对为赋值的赋0
            if (this.containedType instanceof IrIntegerType) {
                // 全局变量
                String temp = this.getName() + " = dso_local global "
                        + this.containedType.irOutput().get(0)
                        + " " + this.init.irOutput().get(0) + "\n";
                ret.add(temp);
            } else if (this.containedType instanceof  IrIntegerType) {
                // 全局变量数组
                /* TODO : 本次作业不涉及数组 */
                ret = ret;
            } else {
                System.out.println("ERROR in IrGlobalVariable : should not reach here");
            }
        }
        return ret;
    }

    /* 返回i32的初值 */
    public int getIntInit() {
        Integer integer = Integer.valueOf(this.init.irOutput().get(0));
        return integer.intValue();
    }
}
