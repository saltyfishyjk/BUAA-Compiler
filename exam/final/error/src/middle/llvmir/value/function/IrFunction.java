package middle.llvmir.value.function;

import middle.llvmir.IrModule;
import middle.llvmir.IrValue;
import middle.llvmir.type.IrFunctionType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.IrNode;
import middle.llvmir.value.basicblock.IrBasicBlock;

import java.util.ArrayList;

/**
 * LLVM IR Function 函数定义
 */
public class IrFunction extends IrValue implements IrNode {
    private ArrayList<IrParam> params = new ArrayList<>(); // 函数形参列表
    private ArrayList<IrBasicBlock> blocks = new ArrayList<>(); // 函数内基本块列表
    private IrModule module; // 父module
    private IrFunctionCnt functionCnt; // 变量名自增计数器

    /* IrValueType应当为IrFunctionType */
    public IrFunction(IrValueType valueType, IrModule module) {
        super(valueType);
        initParams();
        this.module = module;
    }

    public IrFunction(IrValueType valueType, IrModule module, String name) {
        this(valueType, module);
        this.setName(name);
    }

    public IrFunction(IrValueType valueType, IrModule module,
                      String name, IrFunctionCnt functionCnt) {
        this(valueType, module, name);
        this.functionCnt = functionCnt;
    }

    /**
     * 初始化形参列表，主要工作是将来自IrFunctionType的参数类型列表转成参数列表
     */
    private void initParams() {
        if (this.getValueType() instanceof IrFunctionType) {
            IrFunctionType type = (IrFunctionType) this.getValueType();
            ArrayList<IrValueType> valueTypes = type.getParamTypes();
            ArrayList<IrValue> values = type.getParams();
            int len = valueTypes.size();
            for (int i = 0; i < len; i++) {
                // IrParam param = new IrParam(temp.get(i), i);
                IrValue value = values.get(i);
                IrParam param = new IrParam(valueTypes.get(i), i);
                param.setName(value.getName());
                // param.setName("%_LocalVariable" + i);
                param.setDimension(value.getDimension());
                param.setDimension1(value.getDimension1());
                param.setDimension2(value.getDimension2());
                param.setName(value.getName());
                this.params.add(param);
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

    @Override
    public ArrayList<String> irOutput() {
        /* 函数签名 */
        StringBuilder functionName = new StringBuilder("\ndefine dso_local ");
        // functionName.append(this.getValueType().irOutput());
        ArrayList<String> typeOut = this.getValueType().irOutput();
        if (typeOut == null || typeOut.size() != 1) {
            System.out.println("ERROR in IrFunction.irOutput : should not reach here");
        } else {
            functionName.append(typeOut.get(0));
        }
        functionName.append(" ");
        functionName.append(this.getName());
        functionName.append("(");
        /* 形参 */
        for (IrParam param : this.params) {
            // 每个形参返回的字符串列表应该只有一个字符串
            ArrayList<String> temp = param.irOutput();
            if (temp == null || temp.size() == 0 || temp.size() > 1) {
                System.out.println("ERROR in IrFunction.irOutput : should not reach here");
            } else {
                functionName.append(temp.get(0) + ", ");
            }
        }
        /* 去掉最后可能多余的", " */
        int len = functionName.length();
        if (functionName.charAt(len - 2) == ',' && functionName.charAt(len - 1) == ' ') {
            // 由于至少有define等元素在，因此不用考虑index out of的问题
            /* TODO : 检查正确性 */
            functionName.replace(len - 2, len - 1, "");
        }
        functionName.append(") #0 {\n");
        ArrayList<String> ret = new ArrayList<>();
        ret.add(functionName.toString());
        /* 基本块 */
        for (IrBasicBlock basicBlock : this.blocks) {
            ArrayList<String> temp = basicBlock.irOutput();
            if (temp != null && temp.size() != 0) {
                ret.addAll(temp);
            }
        }
        /* 函数结尾大括号 */
        String functionEnd = "}\n";
        ret.add(functionEnd);

        ArrayList<String> temp = new ArrayList<>();

        for (String s : ret) {
            if (s.contains("dso_local")) {
                temp.add(s);
            }
        }

        for (String s : ret) {
            if (s.contains("alloca") && !(s.contains("dso_local"))) {
                temp.add(s);
            }
        }
        for (String s : ret) {
            if (!s.contains("alloca") && !(s.contains("dso_local"))) {
                temp.add(s);
            }
        }

        ret = temp;

        return ret;
    }

    public ArrayList<IrBasicBlock> getBlocks() {
        return this.blocks;
    }
}
