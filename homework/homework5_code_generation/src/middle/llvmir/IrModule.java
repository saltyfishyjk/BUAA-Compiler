package middle.llvmir;

import middle.llvmir.value.IrFunction;
import middle.llvmir.value.IrGlobalVariable;
import middle.llvmir.value.IrNode;

import java.util.ArrayList;

public class IrModule implements IrNode {
    private ArrayList<IrGlobalVariable> globalVariables; // Module中的全局变量
    private ArrayList<IrFunction> functions; // Module中的函数

    public IrModule() {
        this.functions = new ArrayList<>();
        this.globalVariables = new ArrayList<>();
    }

    public void addIrFunction(IrFunction function) {
        this.functions.add(function);
    }

    public void addIrGlobalVariables(IrGlobalVariable variable) {
        this.globalVariables.add(variable);
    }

    @Override
    public ArrayList<String> irOutput() {
        ArrayList<String> ret = new ArrayList<>();
        for (IrGlobalVariable index : globalVariables) {
            ret.addAll(index.irOutput());
        }
        return ret;
    }
}
