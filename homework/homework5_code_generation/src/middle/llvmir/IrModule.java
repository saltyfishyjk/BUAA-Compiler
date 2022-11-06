package middle.llvmir;

import middle.llvmir.value.function.IrFunction;
import middle.llvmir.value.globalvariable.IrGlobalVariable;
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
        String s = "declare i32 @getint()\n" +
                "declare void @putint(i32)\n" +
                "declare void @putch(i32)\n\n";
        ret.add(s);
        for (IrGlobalVariable index : globalVariables) {
            ArrayList<String> temp = index.irOutput();
            if (temp != null && temp.size() != 0) {
                ret.addAll(temp);
            }
        }
        for (IrFunction function : functions) {
            ArrayList<String> temp = function.irOutput();
            if (temp != null && temp.size() != 0) {
                ret.addAll(temp);
            }
        }
        return ret;
    }

    public ArrayList<IrFunction> getFunctions() {
        return functions;
    }

    public ArrayList<IrGlobalVariable> getGlobalVariables() {
        return globalVariables;
    }
}
