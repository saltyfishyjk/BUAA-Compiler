package middle.llvmir.value.instructions;

import frontend.parser.declaration.Decl;
import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.declaration.constant.ConstDef;
import frontend.parser.declaration.constant.constinitval.ConstInitVal;
import frontend.parser.declaration.constant.constinitval.ConstInitValMulti;
import frontend.parser.declaration.variable.VarDecl;
import frontend.parser.expression.ConstExp;
import frontend.parser.statement.blockitem.BlockItemEle;
import frontend.parser.statement.stmt.Stmt;
import frontend.parser.statement.stmt.StmtAssign;
import frontend.parser.statement.stmt.StmtBreak;
import frontend.parser.statement.stmt.StmtContinue;
import frontend.parser.statement.stmt.StmtEle;
import frontend.parser.statement.stmt.StmtExp;
import frontend.parser.statement.stmt.StmtGetint;
import frontend.parser.statement.stmt.StmtPrint;
import frontend.parser.statement.stmt.StmtReturn;
import middle.llvmir.value.basicblock.IrBasicBlock;
import middle.llvmir.value.function.IrFunctionCnt;
import middle.symbol.Symbol;
import middle.symbol.SymbolCon;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;

import java.util.ArrayList;

/**
 * LLVM IR Instruction Builder
 * LLVM IR 指令生成器
 */
public class IrInstructionBuilder {
    private SymbolTable symbolTable; // 当前符号表
    private IrBasicBlock basicBlock; // 父BasicBlock
    private BlockItemEle blockItemEle; // 传入的BlockItemEle
    private ArrayList<IrInstruction> instructions;
    private IrFunctionCnt functionCnt = null;
    /* 以下对象是传入的BlockItem可能的真实类型，如果本对象不是，那么就为null */
    private ConstDecl constDecl = null;
    private VarDecl varDecl = null;
    private StmtAssign stmtAssign = null;
    private StmtBreak stmtBreak = null;
    private StmtContinue stmtContinue = null;
    private StmtReturn stmtReturn = null;
    private StmtGetint stmtGetint = null;
    private StmtPrint stmtPrint = null;
    private StmtExp stmtExp = null;

    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock, IrFunctionCnt functionCnt) {
        this.symbolTable = symbolTable;
        this.basicBlock = basicBlock;
        this.instructions = new ArrayList<>();
        this.functionCnt = functionCnt;
    }

    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock,
                                BlockItemEle blockItemEle,
                                IrFunctionCnt functionCnt) {
        this(symbolTable, basicBlock, functionCnt);
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
            } else if (stmtEle instanceof StmtExp) {
                this.stmtExp = (StmtExp)stmtEle;
                genIrInstructionFromStmtExp();
            } else {
                System.out.println("ERROR in IrInstructionBuilder : should not reach here");
            }
        } else {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        }
        return this.instructions;
    }

    /**
     * 处理SysY中的ConstDecl语句
     * 在中间代码中，对于常值常数，不需要将其翻译为指令
     * 只用在符号表中保存齐SysY的变量名即可，在后续调用中以立即数形式存在
     */
    private void genIrInstructionFromConstDecl() {
        ConstDef first = this.constDecl.getFirst();
        addCon(first);
        for (ConstDef constDef : constDecl.getConstDefs()) {
            addCon(constDef);
        }
    }

    private void addCon(ConstDef constDef) {
        SymbolCon symbolCon;
        if (constDef.getDimension() == 0) {
            // 数值常数
            SymbolType symbolType = SymbolType.CON;
            symbolCon = new SymbolCon(constDef.getName(), symbolType, 0);
            this.symbolTable.addSymol(symbolCon);
            setInitVal(symbolCon, constDef.getConstInitval());
        } else if (constDef.getDimension() == 1) {
            /* TODO : 本次作业不涉及数组 */
        } else if (constDef.getDimension() == 2) {
            /* TODO : 本次作业不涉及数组 */
        } else {
            System.out.println(
                    "ERROR in IrInstructionBuilder.addCon : should not reach here");
        }
    }

    /**
     * 添加符号初值
     */
    private void setInitVal(Symbol symbol, ConstInitVal initVal) {
        int dimension = symbol.getDimension();
        SymbolCon symbolCon = (SymbolCon) symbol; // 在常量解析器中一定是常量
        if (dimension == 0) { // 常量初值
            ConstExp exp = (ConstExp)initVal.getConstInitValEle();
            symbolCon.setInitVal(exp.calcNode(this.symbolTable));
        } else if (dimension == 1) { // 一维数组初值
            ConstInitValMulti initValMulti = (ConstInitValMulti)initVal.getConstInitValEle();
            ArrayList<Integer> temp = new ArrayList<>();
            for (ConstInitVal initVal1 : initValMulti.getConstInitVals()) {
                temp.add(initVal1.calcNode(this.symbolTable));
            }
            symbolCon.setInitval1(temp);
        } else if (dimension == 2) { // 二维数组初值
            ConstInitValMulti initValMulti = (ConstInitValMulti)initVal.getConstInitValEle();
            ArrayList<ArrayList<Integer>> temp1 = new ArrayList<>();
            for (ConstInitVal initVal1 : initValMulti.getConstInitVals()) {
                // initVal1 类型应当是一维数组
                ConstInitValMulti initValMulti1 = (ConstInitValMulti)initVal1.getConstInitValEle();
                ArrayList<Integer> temp2 = new ArrayList<>();
                for (ConstInitVal initVal2 : initValMulti1.getConstInitVals()) {
                    // initVal2应当是常量
                    temp2.add(initVal2.calcNode(this.symbolTable));
                }
                temp1.add(temp2);
            }
            symbolCon.setInitval2(temp1);
        } else {
            System.out.println("ERROR in ConstDefParser.setInitval : should not reach here");
        }
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

    private void genIrInstructionFromStmtExp() {
        /* TODO : 待施工 */
    }

}
