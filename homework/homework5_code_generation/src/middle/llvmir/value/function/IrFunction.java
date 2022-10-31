package middle.llvmir.value.function;

import middle.llvmir.IrModule;
import middle.llvmir.IrValue;
import middle.llvmir.type.IrFunctionType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.basicblock.IrBasicBlock;

import java.util.ArrayList;

/**
 * LLVM IR Function 函数定义
 */
public class IrFunction extends IrValue {
    private ArrayList<IrParam> params = new ArrayList<>(); // 函数形参列表
    private ArrayList<IrBasicBlock> blocks = new ArrayList<>(); // 函数内基本块列表
    private IrModule module; // 父module

    /* IrValueType应当为IrFunctionType */
    public IrFunction(IrValueType valueType, IrModule module) {
        super(valueType);
        initParams();
        this.module = module;
    }

    /**
     * 初始化形参列表，主要工作是将来自IrFunctionType的参数类型列表转成参数列表
     */
    private void initParams() {
        if (this.getValueType() instanceof IrFunctionType) {
            IrFunctionType type = (IrFunctionType) this.getValueType();
            ArrayList<IrValueType> temp = type.getParamTypes();
            int len = temp.size();
            for (int i = 0; i < len; i++) {
                this.params.add(new IrParam(temp.get(i), i));
            }
        } else {
            System.out.println("ERROR in IrFunction! Expect IrFunctionType!");
        }
    }

    public ArrayList<IrParam> getParams() {
        return params;
    }

    public void addIrBasicBlock(IrBasicBlock block) {
        this.blocks.add(block);
    }

    public void addAllIrBasicBlock(ArrayList<IrBasicBlock> list) {
        for (IrBasicBlock basicBlock : list) {
            this.addIrBasicBlock(basicBlock);
        }
    }
}
