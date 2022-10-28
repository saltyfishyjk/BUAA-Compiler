package middle.llvmir;

import middle.llvmir.value.IrFunction;
import middle.llvmir.value.IrGlobalVariable;

import java.util.ArrayList;

public class IrModule {
    private ArrayList<IrFunction> functions; // Module中的函数
    private ArrayList<IrGlobalVariable> globalVariables; // Module中的全局变量

    public IrModule() {
        /* TODO */
    }

}
