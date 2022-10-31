package middle.llvmir.value.instructions;

import frontend.parser.declaration.Decl;
import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.declaration.variable.VarDecl;
import frontend.parser.statement.blockitem.BlockItemEle;
import frontend.parser.statement.stmt.Stmt;
import frontend.parser.statement.stmt.StmtAssign;
import frontend.parser.statement.stmt.StmtBreak;
import frontend.parser.statement.stmt.StmtContinue;
import frontend.parser.statement.stmt.StmtEle;
import frontend.parser.statement.stmt.StmtGetint;
import frontend.parser.statement.stmt.StmtPrint;
import frontend.parser.statement.stmt.StmtReturn;
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
    private BlockItemEle blockItemEle;
    private ArrayList<IrInstruction> instructions;
    /* 以下对象是传入的BlockItem可能的真实类型，如果本对象不是，那么就为null */
    private ConstDecl constDecl = null;
    private VarDecl varDecl = null;
    private StmtAssign stmtAssign = null;
    private StmtBreak stmtBreak = null;
    private StmtContinue stmtContinue = null;
    private StmtReturn stmtReturn = null;
    private StmtGetint stmtGetint = null;
    private StmtPrint stmtPrint = null;

    public IrInstructionBuilder(SymbolTable symbolTable, IrBasicBlock basicBlock) {
        this.symbolTable = symbolTable;
        this.basicBlock = basicBlock;
        this.instructions = new ArrayList<>();
    }

    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock,
                                BlockItemEle blockItemEle) {
        this(symbolTable, basicBlock);
        this.blockItemEle = blockItemEle;
    }

    /**
     * 分发器，分发到合适的private func
     * 主要原理为通过判断BlockItemEle对象的真正类来判断传入的是哪个参数
     */
    public ArrayList<IrInstruction> genIrInstruction() {
        if (this.blockItemEle instanceof Decl) {
            Decl decl = (Decl)blockItemEle;
            if (decl.getDeclEle() instanceof ConstDecl) {
                // ConstDecl
                this.constDecl = (ConstDecl)decl.getDeclEle();
                genIrInstructionFromConstDecl();
            } else if (decl.getDeclEle() instanceof VarDecl) {
                // VarDecl
                this.varDecl = (VarDecl)decl.getDeclEle();
                genIrInstructionFromVarDecl();
            } else {
                System.out.println("ERROR in IrInstructionBuilder : should not reach here");
            }
        } else if (this.blockItemEle instanceof Stmt) {
            Stmt stmt = (Stmt)this.blockItemEle;
            StmtEle stmtEle = stmt.getStmtEle();
            if (stmtEle instanceof StmtAssign) {
                this.stmtAssign = (StmtAssign) stmtEle;
                genIrInstructionFromStmtAssign();
            } else if (stmtEle instanceof StmtBreak) {
                this.stmtBreak = (StmtBreak)stmtEle;
                genIrInstructionFromStmtBreak();
            } else if (stmtEle instanceof StmtContinue) {
                this.stmtContinue = (StmtContinue)stmtEle;
                genIrInstructionFromStmtContinue();
            } else if (stmtEle instanceof StmtReturn) {
                this.stmtReturn = (StmtReturn)stmtEle;
                genIrInstructionFromStmtReturn();
            } else if (stmtEle instanceof StmtGetint) {
                this.stmtGetint = (StmtGetint)stmtEle;
                genIrInstructionFromStmtGetint();
            } else if (stmtEle instanceof StmtPrint) {
                this.stmtPrint = (StmtPrint)stmtEle;
                genIrInstructionFromStmtPrint();
            } else {
                System.out.println("ERROR in IrInstructionBuilder : should not reach here");
            }
        } else {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        }
        return this.instructions;
    }

    private void genIrInstructionFromConstDecl() {
        /* TODO : 待施工 */
    }

    private void genIrInstructionFromVarDecl() {
        /* TODO : 待施工 */
    }

    private void genIrInstructionFromStmtAssign() {
        /* TODO : 待施工 */
    }

    private void genIrInstructionFromStmtBreak() {
        /* TODO : 本次作业不涉及 */
    }

    private void genIrInstructionFromStmtContinue() {
        /* TODO : 本次作业不涉及 */
    }

    private void genIrInstructionFromStmtReturn() {
        /* TODO : 待施工 */
    }

    private void genIrInstructionFromStmtGetint() {
        /* TODO : 待施工 */
    }

    private void genIrInstructionFromStmtPrint() {
        /* TODO : 待施工 */
    }

}
