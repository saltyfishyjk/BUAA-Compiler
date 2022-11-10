package backend;

import backend.function.MipsFunctionBuilder;
import backend.instruction.Li;
import backend.instruction.Sw;
import backend.symbol.MipsSymbol;
import middle.llvmir.IrModule;
import middle.llvmir.value.function.IrFunction;
import middle.llvmir.value.globalvariable.IrGlobalVariable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Mips代码生成器
 * LLVM IR -> MIPS
 */
public class MipsBuilder {
    private IrModule irModule;

    public MipsBuilder(IrModule irModule) {
        this.irModule = irModule;
    }

    /* 生成MipsModule */
    public MipsModule genMipsModule() {
        MipsModule mipsModule = new MipsModule();
        /* TODO : 生成MipsModule */
        /* 加载全局变量 */
        /* 对于每个符号表而言，其初始状态都应当将全局变量加载进去并可以访问 */
        /* 根据我们的寄存器约定，使用$24即$t8不断li和sw */
        HashMap<String, MipsSymbol> globalVariable = new HashMap<>();
        ArrayList<IrGlobalVariable> variables = irModule.getGlobalVariables();
        int offset = 0;
        for (IrGlobalVariable variable : variables) {
            int value = variable.getIntInit();
            /* 加载到$24 */
            Li li = new Li(24, value);
            mipsModule.addGlobal(li);
            /* 保存到内存 */
            Sw sw = new Sw(24, 28, offset);
            mipsModule.addGlobal(sw);
            MipsSymbol symbol = new MipsSymbol(variable.getName(),
                    28, offset);
            globalVariable.put(symbol.getName(), symbol);
            offset += 4;
        }
        /* 生成函数 */
        ArrayList<IrFunction> irFunctions = this.irModule.getFunctions();
        for (IrFunction function : irFunctions) {
            MipsFunctionBuilder mipsFunctionBuilder = new MipsFunctionBuilder(function,
                    mipsModule, globalVariable);
            mipsModule.addFunction(mipsFunctionBuilder.genMipsFunction());
        }
        return mipsModule;
    }

}
