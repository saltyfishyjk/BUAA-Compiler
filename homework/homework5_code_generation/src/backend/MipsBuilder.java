package backend;

import backend.function.MipsFunctionBuilder;
import middle.llvmir.IrModule;
import middle.llvmir.value.function.IrFunction;

import java.util.ArrayList;

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
        /* TODO : 加载全局变量 */
        /* 生成函数 */
        ArrayList<IrFunction> irFunctions = this.irModule.getFunctions();
        for (IrFunction function : irFunctions) {
            MipsFunctionBuilder mipsFunctionBuilder = new MipsFunctionBuilder(function);
            mipsModule.addFunction(mipsFunctionBuilder.genMipsFunction());
        }
        return mipsModule;
    }

}
