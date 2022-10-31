package middle.llvmir.value.basicblock;

import frontend.parser.statement.Block;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * LLVM IR BasicBlock Builder
 * LLVM IR 基本块生成器
 */
public class IrBasicBlockBuilder {
    private SymbolTable symbolTable;
    private Block block;

    public IrBasicBlockBuilder(SymbolTable symbolTable, Block block) {
        this.symbolTable = symbolTable;
        this.block = block;
    }

    public ArrayList<IrBasicBlock> genIrBasicBlock() {
        /* TODO : 待施工 */
        return null;
    }
}
