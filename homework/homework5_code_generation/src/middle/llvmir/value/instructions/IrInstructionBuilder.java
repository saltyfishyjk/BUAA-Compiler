package middle.llvmir.value.instructions;

import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.statement.blockitem.BlockItemEle;
import middle.llvmir.value.basicblock.IrBasicBlock;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * LLVM IR Instruction Builder
 * LLVM IR 指令生成器
 */
public class IrInstructionBuilder {
    private SymbolTable symbolTable;
    private IrBasicBlock basicBlock; // 父BasicBlock
    private ConstDecl constDecl = null;
    private BlockItemEle blockItemEle;

    public IrInstructionBuilder(SymbolTable symbolTable, IrBasicBlock basicBlock) {
        this.symbolTable = symbolTable;
        this.basicBlock = basicBlock;
    }

    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock,
                                BlockItemEle blockItemEle) {
        this(symbolTable, basicBlock);
        this.blockItemEle = blockItemEle;
    }

    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock,
                                ConstDecl constDecl) {
        this(symbolTable, basicBlock);
        this.constDecl = constDecl;
    }

    /**
     * 分发器，分发到合适的private func
     * 主要原理为通过判断非Null对象来判断传入的是哪个参数
     */
    public ArrayList<IrInstruction> genIrstruction() {
        /* TODO : 待施工 */
        return null;
    }

}
