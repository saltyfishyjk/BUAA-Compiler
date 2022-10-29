package middle.llvmir;

import frontend.parser.CompUnit;
import frontend.parser.declaration.DeclEle;
import frontend.parser.function.FuncDef;
import frontend.parser.statement.blockitem.BlockItemEle;
import frontend.parser.statement.stmt.StmtEle;
import middle.llvmir.value.IrBasicBlock;
import middle.llvmir.value.IrFunction;
import middle.llvmir.value.instructions.IrInstruction;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * IrBuilder
 * 将抽象语法树翻译成LLVM IR
 * 为了精简我们的代码结构，向低耦合高内聚更好地进发，
 * 此次我们将LLVM IR四种层次的解析器置于本类中一起实现
 */
public class IrBuilder {
    private CompUnit compUnit; // 作为构造器参数，为语法树的顶层节点，其蕴含了语法树的全部信息
    private IrModule module; // 顶层Module单元
    private SymbolTable symbolTable; // 当前指向的符号表

    public IrBuilder(CompUnit compUnit) {
        this.compUnit = compUnit;
        this.module = new IrModule(); // 生成新的IrModule
        this.symbolTable = new SymbolTable();
    }

    /**
     * ---------- 生成 LLVM IR Module ----------
     */

    public IrModule genIrModule() {

        return this.module;
    }

    /**
     * ---------- 生成LLVM IR Function ----------
     */

    /* 因为这里生成的IrFunction一定是IrModule的属性，因此只传一个参数即可 */
    public IrFunction genIrFunction(FuncDef funcDef) {
        /* TODO : fill contents */
        return null;
    }

    /**
     * ---------- 生成LLVM IR BasicBlock ----------
     * 通过重载传入参数来处理不同情况
     */

    public ArrayList<IrBasicBlock> genIrBasicBlock(BlockItemEle block) {
        /* TODO : fill contents */
        return null;
    }

    /**
     * ---------- 生成LLVM IR Instruction ----------
     * 通过重载传入参数来处理不同情况
     */

    public ArrayList<IrInstruction> genIrInstruction(DeclEle decl) {
        /* TODO : fill contents */
        return null;
    }

    public ArrayList<IrInstruction> genIrInstruction(StmtEle stmt) {
        /* TODO : fill contents */
        return null;
    }
}
