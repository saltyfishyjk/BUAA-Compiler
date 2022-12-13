package middle.llvmir.value.basicblock;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.declaration.Decl;
import frontend.parser.declaration.DeclEle;
import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.declaration.variable.VarDecl;
import frontend.parser.expression.Cond;
import frontend.parser.expression.multiexp.AddExp;
import frontend.parser.expression.multiexp.EqExp;
import frontend.parser.expression.multiexp.LAndExp;
import frontend.parser.expression.multiexp.LOrExp;
import frontend.parser.expression.multiexp.RelExp;
import frontend.parser.expression.primaryexp.Number;
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
import frontend.parser.terminal.IntConst;
import middle.llvmir.IrValue;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.value.function.IrFunctionCnt;
import middle.llvmir.value.instructions.IrBinaryInst;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionBuilder;
import middle.llvmir.value.instructions.IrInstructionType;
import middle.llvmir.value.instructions.IrLabel;
import middle.llvmir.value.instructions.IrLabelCnt;
import middle.llvmir.value.instructions.terminator.IrBr;
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
    /* 以下为while服务 */
    private IrLabel whileLabel = null;
    private IrLabel endLabel = null;

    public IrBasicBlockBuilder(SymbolTable symbolTable,
                               IrFunctionCnt functionCnt,
                               IrLabel whileLabel,
                               IrLabel endLabel) {
        this.symbolTable = symbolTable;
        this.functionCnt = functionCnt;
        this.whileLabel = whileLabel;
        this.endLabel = endLabel;
    }

    // 传入的元素是Block
    public IrBasicBlockBuilder(SymbolTable symbolTable,
                               Block block,
                               IrFunctionCnt functionCnt,
                               IrLabel whileLabel,
                               IrLabel endLabel) {
        this(symbolTable, functionCnt, whileLabel, endLabel);
        this.block = block;
        this.blockItems = this.block.getBlockItems();
    }

    // 传入的元素是StmtCond
    public IrBasicBlockBuilder(SymbolTable symbolTable,
                               StmtCond stmtCond,
                               IrFunctionCnt functionCnt,
                               IrLabel whileLabel,
                               IrLabel endLabel) {
        this(symbolTable, functionCnt, whileLabel, endLabel);
        this.stmtCond = stmtCond;
    }

    // 传入的元素是StmtWhile
    public IrBasicBlockBuilder(SymbolTable symbolTable,
                               StmtWhile stmtWhile,
                               IrFunctionCnt functionCnt,
                               IrLabel whileLabel,
                               IrLabel endLabel) {
        this(symbolTable, functionCnt, whileLabel, endLabel);
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
                        builder = new IrBasicBlockBuilder(newSymbolTable,
                                (StmtCond)stmtEle, this.functionCnt,
                                this.whileLabel, this.endLabel);
                        break;
                    case 2: // 说明是StmtWhile
                        builder = new IrBasicBlockBuilder(newSymbolTable,
                                (StmtWhile)stmtEle, this.functionCnt,
                                this.whileLabel, this.endLabel);
                        break;
                    case 3: // 说明是Block
                        builder = new IrBasicBlockBuilder(newSymbolTable,
                                (Block)stmtEle, this.functionCnt,
                                this.whileLabel, this.endLabel);
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
                            basicBlock, curEle, this.functionCnt, whileLabel, endLabel);
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
        if (hasElse) {
            this.addAllIrBasicBlocks(genCond(cond, ifLabel, elseLabel));
        } else {
            this.addAllIrBasicBlocks(genCond(cond, ifLabel, endLabel));
        }
        /* 处理if语句块 */
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
                        (Block)ifStmtEle, this.functionCnt,
                        this.whileLabel, this.endLabel);
            } else if (ifStmtEle instanceof StmtCond) {
                builder = new IrBasicBlockBuilder(newSymbolTable,
                        (StmtCond)ifStmtEle, this.functionCnt,
                        this.whileLabel, this.endLabel);
            } else {
                builder = new IrBasicBlockBuilder(newSymbolTable,
                        (StmtWhile)ifStmtEle, this.functionCnt,
                        this.whileLabel, this.endLabel);
            }
            this.addAllIrBasicBlocks(builder.genIrBasicBlock());
            /* 将goto endLabel语句包装为一个IrBasicBlock加入其中 */
            IrGoto irGoto = new IrGoto(endLabel);
            IrBasicBlock temp = new IrBasicBlock("GOTO IF_END");
            temp.addIrInstruction(irGoto);
            this.basicBlocks.add(temp);
        } else if (!(ifStmtEle instanceof StmtNull)) {
            /* StmtEle不是StmtCond, StmtWhile或Block，使用生成*/
            IrInstructionBuilder instructionBuilder = new IrInstructionBuilder(this.symbolTable,
                    ifBlock, ifStmt, this.functionCnt, this.whileLabel, this.endLabel);
            ArrayList<IrInstruction> temp = instructionBuilder.genIrInstruction();
            if (temp != null && temp.size() != 0) {
                ifBlock.addAllIrInstruction(temp);
            }
            /* 将goto endLabel添加到ifBlock末尾 */
            IrGoto irGoto = new IrGoto(endLabel);
            ifBlock.addIrInstruction(irGoto);
            this.basicBlocks.add(ifBlock);
        } else {
            IrGoto irGoto = new IrGoto(endLabel);
            ifBlock.addIrInstruction(irGoto);
            this.basicBlocks.add(ifBlock);
        }
        /* 处理else语句块 */
        if (hasElse) {
            /* 添加跳转到endLabel的语句 */
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
                            (Block)elseStmtEle, this.functionCnt,
                            this.whileLabel, this.endLabel);
                } else if (elseStmtEle instanceof StmtCond) {
                    builder = new IrBasicBlockBuilder(newSymbolTable,
                            (StmtCond)elseStmtEle, this.functionCnt,
                            this.whileLabel, this.endLabel);
                } else {
                    builder = new IrBasicBlockBuilder(newSymbolTable,
                            (StmtWhile)elseStmtEle, this.functionCnt,
                            this.whileLabel, this.endLabel);
                }
                this.addAllIrBasicBlocks(builder.genIrBasicBlock());
            } else if (!(elseStmtEle instanceof StmtNull)) {
                /* StmtEle不是StmtCond, StmtWhile或Block，使用生成 */
                IrInstructionBuilder instructionBuilder = new IrInstructionBuilder(this.symbolTable,
                        elseBlock, elseStmt, this.functionCnt, this.whileLabel, this.endLabel);
                ArrayList<IrInstruction> temp = instructionBuilder.genIrInstruction();
                if (temp != null && temp.size() != 0) {
                    elseBlock.addAllIrInstruction(temp);
                }
                /* 将goto endLabel添加到ifBlock末尾 */
                IrGoto irGoto = new IrGoto(endLabel);
                elseBlock.addIrInstruction(irGoto);
                this.basicBlocks.add(elseBlock);
            } else {
                /* 将goto endLabel添加到ifBlock末尾 */
                IrGoto irGoto = new IrGoto(endLabel);
                elseBlock.addIrInstruction(irGoto);
                this.basicBlocks.add(elseBlock);
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
        /* 处理Cond */
        IrBasicBlock condBlock = new IrBasicBlock("Cond IrBasicBlock");
        /* 条件表达式 */
        LOrExp lorexp = cond.getLorExp();
        /* TODO ：待施工 */
        /* 将所有LAndExp放在一起 */
        ArrayList<LAndExp> landExps = lorexp.getAllOperands();
        int len = landExps.size();
        for (int i = 0; i < len; i++) {
            IrLabel nextLabel = genIrInstructionFromLandExp(landExps.get(i), ifLabel);
            if (nextLabel != null) {
                IrBasicBlock temp = new IrBasicBlock("TEMP");
                temp.addIrInstruction(nextLabel);
                this.basicBlocks.add(temp);
            }
        }
        IrGoto irGoto = new IrGoto(endLabel);
        condBlock.addIrInstruction(irGoto);
        this.basicBlocks.add(condBlock);
        ArrayList<IrBasicBlock> ret = new ArrayList<>();
        return ret;
    }

    /*  */
    private IrLabel genIrInstructionFromLandExp(LAndExp landexp, IrLabel label) {
        ArrayList<EqExp> eqexps = landexp.getAllOperands();
        ArrayList<Token> operators = landexp.getOperators();
        IrBasicBlock block = null;
        int len = eqexps.size();
        IrBinaryInst inst;
        /* 如果有多于1个EqExp，则返回null，否则返回下一组EqExp的label */
        IrLabel ret = null;
        if (len == 1) {
            /* 只有一个EqExp说明没有 && */
            /* 说明等价于 EqExp != 0 */
            IrInstructionBuilder builder = new IrInstructionBuilder();
            IrValue zero = builder.genIrInstructionFromNumber(new Number(new IntConst("0", -1)));
            genIrInstructionFromEqExp(eqexps.get(0), label, true);
        } else {
            /* 有2个及以上的EqExp说明有 && */
            IrValue operand1 = null;
            IrValue operand2 = null;
            int retCnt = IrLabelCnt.getCnt();
            String retName = IrLabelCnt.cntToName(retCnt);
            ret = new IrLabel(retName);
            for (int i = 0; i < len; i++) {
                genIrInstructionFromEqExp(eqexps.get(i), ret, false);
            }
            block = new IrBasicBlock("NEXT LANDEXP");
            IrGoto irGoto = new IrGoto(label);
            block.addIrInstruction(irGoto);
            this.basicBlocks.add(block);
        }
        return ret;
    }

    /* 生成br指令 */
    private void genIrInstructionFromEqExp(EqExp eqexp, IrLabel label, boolean pos) {
        ArrayList<RelExp> relExps = eqexp.getAllOperands();
        ArrayList<Token> operators = eqexp.getOperators();
        /* 初始左操作数 */
        IrValue left = genIrInstructionFromRelExp(relExps.get(0));
        IrValue right = null;
        int len = relExps.size();
        for (int i = 1; i < len - 1; i++) {
            /* 当前右操作数 */
            right = genIrInstructionFromRelExp(relExps.get(i));
            /* 相等性运算表达式 */
            IrBinaryInst inst;
            if (operators.get(i - 1).getType().equals(TokenType.EQL)) {
                inst = new IrBinaryInst(IrIntegerType.get32(),
                        IrInstructionType.Eq, left, right);
            } else {
                inst = new IrBinaryInst(IrIntegerType.get32(),
                        IrInstructionType.Ne, left, right);
            }
            /* 为==或!=计算结果生成新中间变量用以保存 */
            int cnt = this.functionCnt.getCnt();
            String leftName = "%_LocalVariable" + cnt;
            left = new IrValue(IrIntegerType.get32(), leftName);
            inst.setName(left.getName());
            // IrStore store = new IrStore(inst, left);
            IrBasicBlock basicBlock = new IrBasicBlock("EqExp");
            basicBlock.addIrInstruction(inst);
            // basicBlock.addIrInstruction(store);
            this.basicBlocks.add(basicBlock);
        }
        if (len > 1) {
            right = genIrInstructionFromRelExp(relExps.get(len - 1));
        }
        IrBr br;
        if (pos) {
            /* 正常br */
            if (operators.size() == 0) {
                /* 说明该EqExp只有1个RelExp */
                IrInstructionBuilder builder = new IrInstructionBuilder();
                IrValue zero = builder.genIrInstructionFromNumber(new
                        Number(new IntConst("0", -1)));
                br = new IrBr(left, zero, label, IrInstructionType.Bne);
            } else if (operators.get(operators.size() - 1).getType().equals(TokenType.EQL)) {
                br = new IrBr(left, right, label, IrInstructionType.Beq);
            } else {
                br = new IrBr(left, right, label, IrInstructionType.Bne);
            }
        } else {
            /* 取反br */
            if (operators.size() == 0) {
                /* 说明该EqExp只有1个RelExp */
                IrInstructionBuilder builder = new IrInstructionBuilder();
                IrValue zero = builder.genIrInstructionFromNumber(new
                        Number(new IntConst("0", -1)));
                br = new IrBr(left, zero, label, IrInstructionType.Beq);
            } else if (operators.get(operators.size() - 1).getType().equals(TokenType.EQL)) {
                br = new IrBr(left, right, label, IrInstructionType.Bne);
            } else {
                br = new IrBr(left, right, label, IrInstructionType.Beq);
            }
        }
        IrBasicBlock block = new IrBasicBlock("Temp");

        block.addIrInstruction(br);
        this.basicBlocks.add(block);
    }

    private IrValue genIrInstructionFromRelExp(RelExp relExp) {
        ArrayList<AddExp> addExps = relExp.getAllOperands();
        ArrayList<Token> operators = relExp.getOperators();
        int len = addExps.size();
        IrInstructionBuilder builder = new IrInstructionBuilder(this.symbolTable,
                new IrBasicBlock("TEMP"), addExps.get(0), this.functionCnt,
                this.whileLabel, this.endLabel);
        IrBasicBlock basicBlock = new IrBasicBlock("RelExp");
        ArrayList<IrInstruction> instructions = builder.genIrInstruction();
        basicBlock.addAllIrInstruction(instructions);
        IrValue left = builder.getLeft();
        IrValue right;
        for (int i = 1; i < len; i++) {
            builder = new IrInstructionBuilder(this.symbolTable,
                    new IrBasicBlock("TEMP"), addExps.get(i), this.functionCnt,
                    this.whileLabel, this.endLabel);
            instructions = builder.genIrInstruction();
            basicBlock.addAllIrInstruction(instructions);
            right = builder.getLeft();
            /* 关系表达式 */
            IrBinaryInst inst = null;
            Token operator = operators.get(i - 1);
            if (operator.getType().equals(TokenType.LSS)) {
                /* < */
                inst = new IrBinaryInst(IrIntegerType.get32(), IrInstructionType.Lt, left, right);
            } else if (operator.getType().equals(TokenType.GRE)) {
                /* > */
                inst = new IrBinaryInst(IrIntegerType.get32(), IrInstructionType.Gt, left, right);
            } else if (operator.getType().equals(TokenType.LEQ)) {
                /* <= */
                inst = new IrBinaryInst(IrIntegerType.get32(), IrInstructionType.Le, left, right);
            } else if (operator.getType().equals(TokenType.GEQ)) {
                /* >= */
                inst = new IrBinaryInst(IrIntegerType.get32(), IrInstructionType.Ge, left, right);
            } else {
                System.out.println("ERROR In IrBasicBlockBuilder : should not reach here!");
            }

            int cnt = this.functionCnt.getCnt();
            String name = "%_LocalVariable" + cnt;
            left = new IrValue(IrIntegerType.get32(), name);
            inst.setName(name);
            basicBlock.addIrInstruction(inst);
        }
        this.basicBlocks.add(basicBlock);
        return left;
    }

    private ArrayList<IrBasicBlock> genIrBasicBlockFromWhile() {
        /* TODO : 待施工 */
        Cond cond = this.stmtWhile.getCond();

        int whileLabelCnt = IrLabelCnt.getCnt();
        String whileLabelName = IrLabelCnt.cntToName(whileLabelCnt);
        /* 生成whileLabel，标记while条件语句开始的标记 */
        IrLabel whileLabel = new IrLabel(whileLabelName);
        IrBasicBlock basicBlock = new IrBasicBlock("TEMP");
        basicBlock.addIrInstruction(whileLabel);
        this.basicBlocks.add(basicBlock);
        /* 生成ifLabel */
        int ifLabelCnt = IrLabelCnt.getCnt();
        String ifLabelName = IrLabelCnt.cntToName(ifLabelCnt);
        IrLabel ifLabel = new IrLabel(ifLabelName);
        /* 生成endLabel */
        int endLabelCnt = IrLabelCnt.getCnt();
        String endLabelName = IrLabelCnt.cntToName(endLabelCnt);
        IrLabel endLabel = new IrLabel(endLabelName);
        /* while的处理逻辑类似if，唯一区别在于whileStmt末尾应当跳转回whileLabel */
        genCond(cond, ifLabel, endLabel);

        /* 处理whileStmt语句块 */
        Stmt stmt = this.stmtWhile.getStmt();
        StmtEle stmtEle = stmt.getStmtEle();
        IrBasicBlock ifBlock = new IrBasicBlock("If Block");
        /* 首先添加while语句块的label */
        ifBlock.addIrInstruction(ifLabel);
        /* 对于ifStmt, whileStmt, Block这三个情况，需要调用genIrBasicBlock */
        IrBasicBlockBuilder builder = null;
        SymbolTable newSymbolTable = new SymbolTable(this.symbolTable);
        if (stmtEle instanceof  StmtCond ||
                stmtEle instanceof StmtWhile ||
                stmtEle instanceof Block) {
            /* 由于后续内容会解析返回一个IrBasicBlock列表，因此先行将if块的标签打包为IrBasicBlock add进去 */
            this.basicBlocks.add(ifBlock);
            if (stmtEle instanceof Block) {
                builder = new IrBasicBlockBuilder(newSymbolTable,
                        (Block)stmtEle, this.functionCnt, whileLabel, endLabel);
            } else if (stmtEle instanceof StmtCond) {
                builder = new IrBasicBlockBuilder(newSymbolTable,
                        (StmtCond)stmtEle, this.functionCnt, whileLabel, endLabel);
            } else {
                builder = new IrBasicBlockBuilder(newSymbolTable,
                        (StmtWhile)stmtEle, this.functionCnt, whileLabel, endLabel);
            }
            this.addAllIrBasicBlocks(builder.genIrBasicBlock());
            /* 将goto whileLabel语句包装为一个IrBasicBlock加入其中 */
            IrGoto irGoto = new IrGoto(whileLabel);
            IrBasicBlock temp = new IrBasicBlock("GOTO WhileBegin");
            temp.addIrInstruction(irGoto);
            this.basicBlocks.add(temp);
        } else {
            /* StmtEle不是StmtCond, StmtWhile或Block，使用生成*/
            IrInstructionBuilder instructionBuilder = new IrInstructionBuilder(this.symbolTable,
                    ifBlock, stmt, this.functionCnt, whileLabel, endLabel);
            ArrayList<IrInstruction> temp = instructionBuilder.genIrInstruction();
            if (temp != null && temp.size() != 0) {
                ifBlock.addAllIrInstruction(temp);
            }
            /* 将goto whileLabel添加到ifBlock末尾 */
            IrGoto irGoto = new IrGoto(whileLabel);
            ifBlock.addIrInstruction(irGoto);
            this.basicBlocks.add(ifBlock);
        }

        /* 将end label 添加到末尾*/
        IrBasicBlock endBlock = new IrBasicBlock("END LABEL");
        endBlock.addIrInstruction(endLabel);
        this.basicBlocks.add(endBlock);
        return this.basicBlocks;
    }

    /**
     * 判断给定的BlockItemEle的具体类型
     * @param ele : BlockItemEle对象
     * @return TypeCode : 对象的具体类别，用以判断如何处理
     * - 1 : StmtCond
     * - 2 : StmtWhile
     * - 3 : Block
     * ---------- 以上应当调用genIrBasicBlock解析 ----------
     * - 4 : ConstDecl
     * - 5 : VarDecl
     * - 6 : StmtAssign
     * - 7 : StmtBreak
     * - 8 : StmtContinue
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
