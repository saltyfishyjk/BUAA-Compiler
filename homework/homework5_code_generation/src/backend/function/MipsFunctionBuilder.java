package backend.function;

import backend.MipsModule;
import backend.basicblock.MipsBasicBlockBuilder;
import backend.function.MipsFunction;
import middle.llvmir.value.basicblock.IrBasicBlock;
import middle.llvmir.value.function.IrFunction;

import java.util.ArrayList;

/**
 * Mips Function Builder : Mips函数生成器
 */
public class MipsFunctionBuilder {
    private IrFunction irFunction;
    private MipsModule father; // 父module

    public MipsFunctionBuilder(IrFunction irFunction, MipsModule father) {
        this.irFunction = irFunction;
        this.father = father;
    }

    public MipsFunction genMipsFunction() {
        MipsFunction function;
        if (this.irFunction.getName().equals("@main")) {
            // 是main函数
            function = new MipsFunction(father, true);
        } else {
            // 不是main函数
            function = new MipsFunction(father, false);
        }
        ArrayList<IrBasicBlock> basicBlocks = this.irFunction.getBlocks();
        for (IrBasicBlock basicBlock : basicBlocks) {
            MipsBasicBlockBuilder builder = new MipsBasicBlockBuilder(function, basicBlock);
            function.addMipsBasicBlock(builder.genMipsBasicBlock());
        }
        return function;
    }
}
