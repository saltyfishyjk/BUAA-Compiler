package middle.llvmir.value.basicblock;

import frontend.parser.declaration.Decl;
import frontend.parser.declaration.DeclEle;
import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.declaration.variable.VarDecl;
import frontend.parser.expression.Cond;
import frontend.parser.expression.multiexp.LAndExp;
import frontend.parser.expression.multiexp.LOrExp;
import frontend.parser.statement.Block;
import frontend.parser.statement.blockitem.BlockItem;
import frontend.parser.statement.blockitem.BlockItemEle;
import frontend.parser.statement.stmt.Stmt;
import frontend.parser.statement.stmt.StmtAssign;
import frontend.parser.statement.stmt.StmtBreak;
import frontend.parser.statement.stmt.StmtCond;
import frontend.parser.statement.stmt.StmtContinue;
import frontend.parser.statement.stmt.StmtEle;
import frontend.parser.statement.stmt.StmtExp;
import frontend.parser.statement.stmt.StmtGetint;
import frontend.parser.statement.stmt.StmtNull;
import frontend.parser.statement.stmt.StmtPrint;
import frontend.parser.statement.stmt.StmtReturn;
import frontend.parser.statement.stmt.StmtWhile;
import middle.llvmir.value.function.IrFunctionCnt;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionBuilder;
import middle.llvmir.value.instructions.IrLabel;
import middle.llvmir.value.instructions.IrLabelCnt;
import middle.llvmir.value.instructions.terminator.IrGoto;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * LLVM IR BasicBlock Builder
 * LLVM IR 基本块生成器
 */
public class IrBasicBlockBuilder {
    private SymbolTable symbolTable;
    private IrFunctionCnt functionCnt = null;
    private Block block = null;
    private StmtCond stmtCond = null;
    private StmtWhile stmtWhile = null;
    private ArrayList<BlockItem> blockItems = null;
    private ArrayList<IrBasicBlock> basicBlocks = new ArrayList<>();

    public IrBasicBlockBuilder(SymbolTable symbolTable, IrFunctionCnt functionCnt) {
        this.symbolTable = symbolTable;
        this.functionCnt = functionCnt;
    }

    // 传入的元素是Block
    public IrBasicBlockBuilder(SymbolTable symbolTable, Block block, IrFunctionCnt functionCnt) {
        this(symbolTable, functionCnt);
        this.block = block;
        this.blockItems = this.block.getBlockItems();
    }

    // 传入的元素是StmtCond
    public IrBasicBlockBuilder(SymbolTable symbolTable,
                               StmtCond stmtCond,
                               IrFunctionCnt functionCnt) {
        this(symbolTable, functionCnt);
        this.stmtCond = stmtCond;
    }

    // 传入的元素是StmtWhile
    public IrBasicBlockBuilder(SymbolTable symbolTable,
                               StmtWhile stmtWhile,
                               IrFunctionCnt functionCnt) {
        this(symbolTable, functionCnt);
        this.stmtWhile = stmtWhile;
    }

    /**
     *  Block内部有以下几种可能：Block, StmtCond, StmtWhile和其他
     *  - 对于Block, StmtCond, StmtWhile递归调用本方法，将获得的List<IrBasicBlock>加入答案
     *  - 对于其他，while遍历直到结束或遇到块为止，其之前的都打包到一个块中
     */

    /**
     * ---------- 生成LLVM IR BasicBlock ----------
     * 通过重载传入参数来处理不同情况
     */
    public ArrayList<IrBasicBlock> genIrBasicBlock() {
        if (this.block != null) {
            /* 说明传入元素是Block */
            return genIrBasicBlockFromBlock();
        } else if (this.stmtCond != null) {
            /* 说明传入元素是StmtCond */
            return genIrBasicBlockFromCond();
        } else if (this.stmtWhile != null) {
            /* 说明传入的元素是StmtWhile */
            return genIrBasicBlockFromWhile();
        } else {
            System.out.println("ERROR in IrBasicBlockItemBuilder : should not reach here");
        }
        return null;
    }

    private ArrayList<IrBasicBlock> genIrBasicBlockFromBlock() {
        int len = this.blockItems.size();
        int ptr = 0;
        while (ptr < len) {
            BlockItem item = this.blockItems.get(ptr);
            BlockItemEle itemEle = item.getBlockItemEle();
            int typeCode = checkBlockItemEleType(itemEle);
            if (0 < typeCode && typeCode <= 3) {
                /* 说明将要解析的元素是块类，会返回一个基本块列表 */
                /* 生成并进入新的符号表 */
                SymbolTable newSymbolTable = new SymbolTable(this.symbolTable);
                IrBasicBlockBuilder builder = null;
                Stmt stmt = (Stmt)itemEle;
                StmtEle stmtEle = stmt.getStmtEle();
                switch (typeCode) {
                    case 1: // 说明是StmtCond
                        /* TODO : 待施工 */
                        builder = new IrBasicBlockBuilder(newSymbolTable,
                                (StmtCond)stmtEle, this.functionCnt);
                        break;
                    case 2: // 说明是StmtWhile
                        /* TODO : 待施工 */
                        builder = new IrBasicBlockBuilder(newSymbolTable,
                                (StmtWhile)stmtEle, this.functionCnt);
                        break;
                    case 3: // 说明是Block
                        /* TODO : 待施工 */
                        builder = new IrBasicBlockBuilder(newSymbolTable,
                                (Block)stmtEle, this.functionCnt);
                        break;
                    default:
                        System.out.
                                println("ERROR in IrBasicBlockItemBuilder : should not reach here");
                        break;
                }
                this.addAllIrBasicBlocks(builder.genIrBasicBlock());
                ptr += 1;
            } else if (typeCode <= 12) {
                /* 说明将要解析的元素是指令，一直解析直到元素末尾或遇到块类元素 */
                IrBasicBlock basicBlock = new IrBasicBlock("TEMP NO NAME");
                while (ptr < len) {
                    /* 始终解析ptr指向的BlockItemEle，并加到当前的IrBasicBlock中，直到结束或遇到块类 */
                    BlockItem curBlockItem = this.blockItems.get(ptr);
                    BlockItemEle curEle = curBlockItem.getBlockItemEle();
                    int curTypeCode = checkBlockItemEleType(curEle);
                    if (0 < curTypeCode && curTypeCode <= 3) {
                        break;
                    } else if (curTypeCode == 13) {
                        ptr += 1;
                        continue;
                    } else if (curTypeCode <= 0 || curTypeCode > 13) {
                        System.out.
                                println("ERROR in IrBasicBlockItemBuilder : should not reach here");
                    }
                    IrInstructionBuilder irInstructionBuilder = new
                            IrInstructionBuilder(this.symbolTable,
                            basicBlock, curEle, this.functionCnt);
                    ArrayList<IrInstruction> temp = irInstructionBuilder.genIrInstruction();
                    if (temp != null && temp.size() != 0) {
                        basicBlock.addAllIrInstruction(temp);
                    }
                    ptr += 1;
                }
                this.basicBlocks.add(basicBlock);
            } else if (typeCode == 13) {
                /* 什么也不做 */
                ptr += 1;
            } else {
                System.out.println("ERROR in IrBasicBlockItemBuilder : should not reach here");
            }
        }
        return this.basicBlocks;
    }

    /**
     * SysY支持形如if， if-else， if-else if- else等形式的条件语句
     */
    private ArrayList<IrBasicBlock> genIrBasicBlockFromCond() {
        /* TODO : 待施工 */
        /* 构造if块标签 */
        int ifLabelCnt = IrLabelCnt.getCnt();
        String ifLabelName = IrLabelCnt.cntToName(ifLabelCnt);
        IrLabel ifLabel = new IrLabel(ifLabelName);
        /* 预留else块标签 */
        int elseLabelCnt = -1;
        String elseLabelName = null;
        IrLabel elseLabel = null;
        /* 标记是否有else块 */
        boolean hasElse = this.stmtCond.hasElse();
        if (hasElse) {
            elseLabelCnt = IrLabelCnt.getCnt();
            elseLabelName = IrLabelCnt.cntToName(elseLabelCnt);
            elseLabel = new IrLabel(elseLabelName);
        }
        /* if-else块结束位置的标签 */
        int endLabelCnt = IrLabelCnt.getCnt();
        String endLabelName = IrLabelCnt.cntToName(endLabelCnt);
        IrLabel endLabel = new IrLabel(endLabelName);
        /* 处理Cond */
        Cond cond = this.stmtCond.getCond();
        if (!hasElse) {
            this.addAllIrBasicBlocks(genCond(cond, ifLabel, endLabel));
        } else {
            this.addAllIrBasicBlocks(genCond(cond, ifLabel, endLabel, elseLabel));
        }
        /* 处理if语句块 */
        /* TODO : 待施工 */
        Stmt ifStmt = this.stmtCond.getIfStmt();
        StmtEle ifStmtEle = ifStmt.getStmtEle();
        IrBasicBlock ifBlock = new IrBasicBlock("If Block");
        /* 首先添加if语句块的label */
        ifBlock.addIrInstruction(ifLabel);
        /* 对于ifStmt, whileStmt, Block这三个情况，需要调用genIrBasicBlock */
        IrBasicBlockBuilder builder = null;
        SymbolTable newSymbolTable = new SymbolTable(this.symbolTable);
        if (ifStmtEle instanceof  StmtCond ||
                ifStmtEle instanceof StmtWhile ||
                ifStmtEle instanceof Block) {
            /* 由于后续内容会解析返回一个IrBasicBlock列表，因此先行将if块的标签打包为IrBasicBlock add进去 */
            this.basicBlocks.add(ifBlock);
            if (ifStmtEle instanceof Block) {
                builder = new IrBasicBlockBuilder(newSymbolTable,
                        (Block)ifStmtEle, this.functionCnt);
            } else if (ifStmtEle instanceof StmtCond) {
                builder = new IrBasicBlockBuilder(newSymbolTable,
                        (StmtCond)ifStmtEle, this.functionCnt);
            } else {
                builder = new IrBasicBlockBuilder(newSymbolTable,
                        (StmtWhile)ifStmtEle, this.functionCnt);
            }
            this.addAllIrBasicBlocks(builder.genIrBasicBlock());
            /* 将goto endLabel语句包装为一个IrBasicBlock加入其中 */
            IrGoto irGoto = new IrGoto(endLabel);
            IrBasicBlock temp = new IrBasicBlock("GOTO IF_END");
            temp.addIrInstruction(irGoto);
            this.basicBlocks.add(temp);
        } else {
            /* StmtEle不是StmtCond, StmtWhile或Block，使用生成*/
            IrInstructionBuilder instructionBuilder = new IrInstructionBuilder(this.symbolTable,
                    ifBlock, ifStmt, this.functionCnt);
            ArrayList<IrInstruction> temp = instructionBuilder.genIrInstruction();
            if (temp != null && temp.size() != 0) {
                ifBlock.addAllIrInstruction(temp);
            }
            /* 将goto endLabel添加到ifBlock末尾 */
            IrGoto irGoto = new IrGoto(endLabel);
            ifBlock.addIrInstruction(irGoto);
            this.basicBlocks.add(ifBlock);
        }
        /* 处理else语句块 */
        if (hasElse) {
            Stmt elseStmt = this.stmtCond.getElseStmt();
            StmtEle elseStmtEle = elseStmt.getStmtEle();
            IrBasicBlock elseBlock = new IrBasicBlock("Else Block");
            /* 添加else块label */
            elseBlock.addIrInstruction(elseLabel);
            if (elseStmtEle instanceof  StmtCond ||
                    elseStmtEle instanceof StmtWhile ||
                    elseStmtEle instanceof Block) {
                /* 由于后续内容会解析返回一个IrBasicBlock列表，因此先行将else块的标签打包为IrBasicBlock add进去 */
                newSymbolTable = new SymbolTable(this.symbolTable);
                this.basicBlocks.add(elseBlock);
                if (elseStmtEle instanceof Block) {
                    builder = new IrBasicBlockBuilder(newSymbolTable,
                            (Block)elseStmtEle, this.functionCnt);
                } else if (elseStmtEle instanceof StmtCond) {
                    builder = new IrBasicBlockBuilder(newSymbolTable,
                            (StmtCond)elseStmtEle, this.functionCnt);
                } else {
                    builder = new IrBasicBlockBuilder(newSymbolTable,
                            (StmtWhile)elseStmtEle, this.functionCnt);
                }
                this.addAllIrBasicBlocks(builder.genIrBasicBlock());
            } else {
                /* StmtEle不是StmtCond, StmtWhile或Block，使用生成 */
                IrInstructionBuilder instructionBuilder = new IrInstructionBuilder(this.symbolTable,
                        ifBlock, ifStmt, this.functionCnt);
                ArrayList<IrInstruction> temp = instructionBuilder.genIrInstruction();
                if (temp != null && temp.size() != 0) {
                    ifBlock.addAllIrInstruction(temp);
                }
                /* 将goto endLabel添加到ifBlock末尾 */
                IrGoto irGoto = new IrGoto(endLabel);
                ifBlock.addIrInstruction(irGoto);
                this.basicBlocks.add(ifBlock);
            }
        }
        /* 将end label 添加到末尾*/
        IrBasicBlock endBlock = new IrBasicBlock("END LABEL");
        endBlock.addIrInstruction(endLabel);
        this.basicBlocks.add(endBlock);
        return this.basicBlocks;
    }

    /* 处理只有if的条件 */
    private ArrayList<IrBasicBlock> genCond(Cond cond, IrLabel ifLabel, IrLabel endLabel) {
        ArrayList<IrBasicBlock> ret = new ArrayList<>();
        /* 处理Cond */
        IrBasicBlock condBlock = new IrBasicBlock("Cond IrBasicBlock");
        /* 条件表达式 */
        LOrExp lorexp = cond.getLorExp();
        // 第一个参数 first
        LAndExp first = lorexp.getFirst();
        // first || a1 || a2 ...
        ArrayList<LAndExp> landexps = lorexp.getOperands();
        return ret;
    }

    /* 处理有if-else的条件 */
    private ArrayList<IrBasicBlock> genCond(Cond cond,
                                            IrLabel ifLabel,
                                            IrLabel endLabel,
                                            IrLabel elseLabel) {
        ArrayList<IrBasicBlock> ret = new ArrayList<>();
        /* TODO : 待施工 */
        return ret;
    }

    private ArrayList<IrBasicBlock> genIrBasicBlockFromWhile() {
        /* TODO : 待施工 */
        return this.basicBlocks;
    }

    /**
     * 判断给定的BlockItemEle的具体类型
     * @param ele : BlockItemEle对象
     * @return TypeCode : 对象的具体类别，用以判断如何处理
     * - 1 : StmtCond : TODO : 本次作业不涉及条件
     * - 2 : StmtWhile : TODO : 本次作业不涉及循环
     * - 3 : Block
     * ---------- 以上应当调用genIrBasicBlock解析 ----------
     * - 4 : ConstDecl
     * - 5 : VarDecl
     * - 6 : StmtAssign
     * - 7 : StmtBreak : TODO : 本次作业不涉及循环
     * - 8 : StmtContinue : TODO : 本次作业不涉及循环
     * - 9 : StmtReturn
     * - 10 : StmtGetint
     * - 11 : StmtPrint
     * - 12 : StmtExp
     * - 13 : StmtNull : 只有分号，不需要处理
     * ---------- 以上应当调用genIrInstruction解析
     */
    private int checkBlockItemEleType(BlockItemEle ele) {
        int ret = 0;
        if (ele instanceof Decl) {
            // Decl
            Decl decl = (Decl) ele;
            DeclEle declEle = decl.getDeclEle();
            if (declEle instanceof ConstDecl) {
                // ConstDecl
                ret = 4;
            } else if (declEle instanceof VarDecl) {
                // VarDecl
                ret = 5;
            } else {
                System.out.println("ERROR in IrBasicBlockBuilder : should not reach here");
            }
        } else if (ele instanceof Stmt) {
            // Stmt
            Stmt stmt = (Stmt)ele;
            StmtEle stmtEle = stmt.getStmtEle();
            if (stmtEle instanceof StmtCond) {
                ret = 1;
            } else if (stmtEle instanceof StmtWhile) {
                ret = 2;
            } else if (stmtEle instanceof Block) {
                ret = 3;
            } else if (stmtEle instanceof StmtAssign) {
                ret = 6;
            } else if (stmtEle instanceof StmtBreak) {
                ret = 7;
            } else if (stmtEle instanceof StmtContinue) {
                ret = 8;
            } else if (stmtEle instanceof StmtReturn) {
                ret = 9;
            } else if (stmtEle instanceof StmtGetint) {
                ret = 10;
            } else if (stmtEle instanceof StmtPrint) {
                ret = 11;
            } else if (stmtEle instanceof StmtExp) {
                ret = 12;
            } else if (stmtEle instanceof StmtNull) {
                ret = 13;
            } else {
                System.out.println("ERROR in IrBasicBlockBuilder : should not reach here");
            }
        } else {
            System.out.println("ERROR in IrBasicBlockBuilder : should not reach here");
        }
        return ret;
    }

    private void addAllIrBasicBlocks(ArrayList<IrBasicBlock> blocks) {
        this.basicBlocks.addAll(blocks);
    }
}
