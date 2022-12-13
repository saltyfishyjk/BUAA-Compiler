package middle.llvmir.value.globalvariable;

import middle.llvmir.IrUser;
import middle.llvmir.type.IrArrayType;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrPointerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.IrNode;
import middle.llvmir.value.constant.IrConstant;
import middle.llvmir.value.constant.IrConstantArray;
import middle.llvmir.value.constant.IrConstantInt;

import java.util.ArrayList;

/**
 * LLVM 全局变量
 */
public class IrGlobalVariable extends IrUser implements IrNode {
    private boolean isConst; // 标记是否是常量
    private IrConstant init; // 初始化的值
    private IrValueType containedType;
    private int dimension = 0;
    private int dimension1 = 0;
    private int dimension2 = 0;

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
        if (this.init instanceof IrConstantInt) {
            this.dimension = 0;
        } else {
            this.dimension = ((IrConstantArray)this.init).getDimension();
            if (this.dimension == 1) {
                this.dimension1 = ((IrConstantArray)this.init).getDimension1();
            } else {
                this.dimension1 = ((IrConstantArray)this.init).getDimension1();
                this.dimension2 = ((IrConstantArray)this.init).getDimension2();
            }
        }
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
                // 0维全局数值常量
                String temp = this.getName() + " = dso_local global "
                        + this.containedType.irOutput().get(0)
                        + " " + this.init.irOutput().get(0) + "\n";
                ret.add(temp);
            } else if (this.getValueType() instanceof IrArrayType) {
                // 1/2维全局数组常量
                StringBuilder sb = new StringBuilder();
                sb.append(this.getName() + " = dso_local global "
                        + this.containedType.irOutput().get(0)
                        + " " + this.init.irOutput().get(0) + "\n");
                ret.add(sb.toString());
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
                StringBuilder sb = new StringBuilder();
                sb.append(this.getName() + " = dso_local global "
                        + this.containedType.irOutput().get(0)
                        + " " + this.init.irOutput().get(0) + "\n");
                ret.add(sb.toString());
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

    /* 返回i32数组初值 */
    public ArrayList<Integer> getIntInit1() {
        IrConstantArray constantArray = ((IrConstantArray)this.init);
        int dimension1 = constantArray.getDimension1();
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = 0; i < dimension1; i++) {
            if (i >= constantArray.getConstants1().size()) {
                break;
            }
            ret.add(Integer.valueOf(constantArray.getConstants1().get(i).irOutput().get(0)));
        }
        return ret;
    }

    public ArrayList<ArrayList<Integer>> getIntInit2() {
        IrConstantArray constantArray = (IrConstantArray)this.init;
        int dimension1 = constantArray.getDimension1();
        int dimension2 = constantArray.getDimension2();
        ArrayList<ArrayList<Integer>> ret = new ArrayList<>();
        for (int i = 0; i < dimension1; i++) {
            if (i >= constantArray.getConstants2().size()) {
                break;
            }
            ArrayList<Integer> temp = new ArrayList<>();
            for (int j = 0; j < dimension2; j++) {
                temp.add(Integer.valueOf(constantArray.
                        getConstants2().get(i).get(j).irOutput().get(0)));
            }
            ret.add(temp);
        }
        return ret;
    }

    public int getDimension() {
        return this.dimension;
    }

    public int getDimension1() {
        return this.dimension1;
    }

    public int getDimension2() {
        return this.dimension2;
    }
}
