package backend.function;

import backend.MipsModule;
import backend.RegisterFile;
import backend.basicblock.MipsBasicBlockBuilder;
import backend.function.MipsFunction;
import backend.symbol.MipsSymbol;
import backend.symbol.MipsSymbolTable;
import middle.llvmir.value.basicblock.IrBasicBlock;
import middle.llvmir.value.function.IrFunction;
import middle.llvmir.value.function.IrParam;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Mips Function Builder : Mips函数生成器
 */
public class MipsFunctionBuilder {
    private IrFunction irFunction;
    private MipsModule father; // 父module
    private MipsSymbolTable table;

    public MipsFunctionBuilder(IrFunction irFunction, MipsModule father) {
        this.irFunction = irFunction;
        this.father = father;
    }

    public MipsFunctionBuilder(IrFunction irFunction,
                               MipsModule father,
                               HashMap<String, MipsSymbol> globalVariable) {
        this(irFunction, father);
        initMipsSymbolTable(globalVariable);
    }

    /* 初始化符号表
    * 1. 将全局变量装入
    * 2. 将函数参数装入 */
    public void initMipsSymbolTable(HashMap<String, MipsSymbol> globalVariable) {
        RegisterFile registerFile = new RegisterFile();
        this.table = new MipsSymbolTable(registerFile);
        registerFile.setTable(this.table);
        /* 将全局变量加入符号表，同时他们不在寄存器中，因此不需要改动寄存器表 */
        for (String index : globalVariable.keySet()) {
            this.table.addSymbol(index, globalVariable.get(index));
        }
        /* 将函数参数（如果有）加入符号表，并将在寄存器中的写入寄存器表 */
        ArrayList<IrParam> params = this.irFunction.getParams();
        int cnt = params.size();
        int index = 0;
        while (index < cnt) {
            IrParam target = null;
            for (IrParam param : params) {
                if (param.getRank() == index) {
                    target = param;
                    break;
                }
            }
            String name = target.getName();
            MipsSymbol symbol = null;
            if (index < 4) {
                // 在$a寄存器中, $a0-$a3 = $4-$7
                symbol = new MipsSymbol(name, 30, true, index + 4, false);
                // 修改寄存器表的记录
                registerFile.addSymbol(index + 4, symbol);
            } else {
                // 在内存中
                symbol = new MipsSymbol(name, 30, false, true, (index - 4) * 4, false);
                // 修改符号表中的偏移
                this.table.addOffset(4);
            }
            this.table.addSymbol(name, symbol);
            index += 1;
        }
    }

    public MipsFunction genMipsFunction() {
        MipsFunction function;
        if (this.irFunction.getName().equals("@main")) {
            // 是main函数
            function = new MipsFunction(father, true,
                    irFunction.getName().substring(1), this.table);
        } else {
            // 不是main函数
            function = new MipsFunction(father, false,
                    irFunction.getName().substring(1), this.table);
        }
        ArrayList<IrBasicBlock> basicBlocks = this.irFunction.getBlocks();
        for (IrBasicBlock basicBlock : basicBlocks) {
            MipsBasicBlockBuilder builder = new MipsBasicBlockBuilder(function, basicBlock);
            function.addMipsBasicBlock(builder.genMipsBasicBlock());
        }
        return function;
    }
}
