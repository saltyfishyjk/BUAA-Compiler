package backend;

import middle.llvmir.IrModule;

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
        return mipsModule;
    }

}
