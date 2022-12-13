package middle.llvmir.value.instructions;

import frontend.lexer.Token;
import frontend.parser.declaration.Decl;
import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.declaration.constant.ConstDef;
import frontend.parser.declaration.constant.constinitval.ConstInitVal;
import frontend.parser.declaration.constant.constinitval.ConstInitValMulti;
import frontend.parser.declaration.variable.VarDecl;
import frontend.parser.declaration.variable.initval.InitVal;
import frontend.parser.declaration.variable.initval.InitValEle;
import frontend.parser.declaration.variable.initval.InitVals;
import frontend.parser.declaration.variable.vardef.VarDef;
import frontend.parser.declaration.variable.vardef.VarDefEle;
import frontend.parser.declaration.variable.vardef.VarDefInit;
import frontend.parser.declaration.variable.vardef.VarDefNull;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.Exp;
import frontend.parser.expression.FuncRParams;
import frontend.parser.expression.multiexp.AddExp;
import frontend.parser.expression.multiexp.MulExp;
import frontend.parser.expression.primaryexp.LVal;
import frontend.parser.expression.primaryexp.Number;
import frontend.parser.expression.primaryexp.PrimaryExp;
import frontend.parser.expression.primaryexp.PrimaryExpEle;
import frontend.parser.expression.primaryexp.PrimaryExpExp;
import frontend.parser.expression.unaryexp.UnaryExp;
import frontend.parser.expression.unaryexp.UnaryExpEle;
import frontend.parser.expression.unaryexp.UnaryExpFunc;
import frontend.parser.expression.unaryexp.UnaryExpOp;
import frontend.parser.expression.unaryexp.UnaryOp;
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
import frontend.lexer.TokenType;

import frontend.parser.terminal.FormatString;
import middle.llvmir.IrValue;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.basicblock.IrBasicBlock;
import middle.llvmir.value.function.IrFunction;
import middle.llvmir.value.function.IrFunctionCnt;
import middle.llvmir.value.instructions.memory.IrAlloca;
import middle.llvmir.value.instructions.memory.IrLoad;
import middle.llvmir.value.instructions.memory.IrStore;
import middle.llvmir.value.instructions.terminator.IrCall;
import middle.llvmir.value.instructions.terminator.IrGoto;
import middle.llvmir.value.instructions.terminator.IrRet;
import middle.symbol.Symbol;
import middle.symbol.SymbolCon;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;
import middle.symbol.SymbolVar;

import java.util.ArrayList;

/**
 * LLVM IR Instruction Builder
 * LLVM IR 指令生成器
 * 需要注意的是，这里已经排除了StmtCond, StmtWhile, StmtBlock, Stmt
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
    /* 以下对象是传入Stmt时的可能*/
    private Stmt stmt = null;
    /* 以下对象是传入AddExp时的可能 */
    private AddExp addExp = null;
    private IrValue left = null;
    /* 以下用于处理continue和break */
    private IrLabel whileLabel = null;
    private IrLabel endLabel = null;

    public IrInstructionBuilder() {

    }

    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock,
                                IrFunctionCnt functionCnt,
                                IrLabel whileLabel,
                                IrLabel endLabel) {
        this.symbolTable = symbolTable;
        this.basicBlock = basicBlock;
        this.instructions = new ArrayList<>();
        this.functionCnt = functionCnt;
        this.whileLabel = whileLabel;
        this.endLabel = endLabel;
    }

    /* 待解析元素是BlockItemEle */
    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock,
                                BlockItemEle blockItemEle,
                                IrFunctionCnt functionCnt,
                                IrLabel whileLabel,
                                IrLabel endLabel) {
        this(symbolTable, basicBlock, functionCnt, whileLabel, endLabel);
        this.blockItemEle = blockItemEle;
    }

    /* 待解析元素是Stmt，主要针对if和while */
    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock,
                                Stmt stmt,
                                IrFunctionCnt functionCnt,
                                IrLabel whileLabel,
                                IrLabel endLabel) {
        this(symbolTable, basicBlock, functionCnt, whileLabel, endLabel);
        this.stmt = stmt;
    }

    /* 待解析元素是AddExp，主要针对RelExp的组成部分 */
    public IrInstructionBuilder(SymbolTable symbolTable,
                                IrBasicBlock basicBlock,
                                AddExp addExp,
                                IrFunctionCnt functionCnt,
                                IrLabel whileLabel,
                                IrLabel endLabel) {
        this(symbolTable, basicBlock, functionCnt, whileLabel, endLabel);
        this.addExp = addExp;
    }

    /**
     * 分发器，分发到合适的private func
     * 主要原理为通过判断BlockItemEle对象的真正类来判断传入的是哪个参数
     */
    public ArrayList<IrInstruction> genIrInstruction() {
        /* 传入元素来自于BlockItemEle */
        if (this.blockItemEle != null) {
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
        } else if (this.addExp != null) {
            genIrInstructionFromAddExp(addExp, false);
        } else {
            StmtEle stmtEle = this.stmt.getStmtEle();
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
        ArrayList<ConstDef> constDefs = this.constDecl.getConstDefs();
        if (constDefs != null && constDefs.size() != 0) {
            for (ConstDef constDef : constDefs) {
                addCon(constDef);
            }
        }
    }

    /**
     * 辅助函数，主要目的是复用代码
     */
    private void addCon(ConstDef constDef) {
        /* TODO 需要将常量数组的值保存到fp中 */
        SymbolCon symbolCon;
        IrIntegerType type = IrIntegerType.get32();
        int cnt = this.functionCnt.getCnt();
        String name = "%_LocalConst" + cnt;
        if (constDef.getDimension() == 0) {
            // 数值常数
            SymbolType symbolType = SymbolType.CON;
            symbolCon = new SymbolCon(constDef.getName(), symbolType, 0);
            this.symbolTable.addSymol(symbolCon);
            setInitVal(symbolCon, constDef.getConstInitval());
            IrValue value = new IrValue(type,
                    String.valueOf(symbolCon.getInitVal()));
            symbolCon.setValue(value);
        } else if (constDef.getDimension() == 1) {
            SymbolType symbolType = SymbolType.CON1;
            symbolCon = new SymbolCon(constDef.getName(), symbolType, 1);
            this.symbolTable.addSymol(symbolCon);
            setInitVal(symbolCon, constDef.getConstInitval());
            // IrValue value = new IrValue(type, "1-D ARRAY");
            IrValue value = new IrValue(type, name);
            value.setDimension(1); // 标记是1维数组
            value.setDimension1(symbolCon.getInitval1().size());
            value.setInits1(symbolCon.getInitval1()); // 将1维数组的初始值传入
            symbolCon.setValue(value);
            /* 生成IrAlloca指令 */
            IrAlloca irAlloca = new IrAlloca(type, value);
            irAlloca.setDimension(1);
            irAlloca.setDimension1(value.getDimension1());
            irAlloca.setName(name);
            this.instructions.add(irAlloca);
            int i = 0;
            /* 使用IrStore将数组初值保存到内存中 */
            ArrayList<Integer> inits = symbolCon.getInitval1();
            for (Integer index : inits) {
                IrValue val = new IrValue(IrIntegerType.get32(), String.valueOf(index));
                IrStore store = new IrStore(val, value, 0, 1, -1, i, -1, -1);
                this.instructions.add(store);
                i += 1;
            }
        } else if (constDef.getDimension() == 2) {
            SymbolType symbolType = SymbolType.CON2;
            symbolCon = new SymbolCon(constDef.getName(), symbolType, 2);
            this.symbolTable.addSymol(symbolCon);
            setInitVal(symbolCon, constDef.getConstInitval());
            // IrValue value = new IrValue(type, "2-D ARRAY");
            IrValue value = new IrValue(type, name);
            value.setDimension(2); // 标记是2维数组
            value.setDimension1(symbolCon.getInitval2().size()); // 标记第1维长度
            value.setDimension2(symbolCon.getInitval2().get(0).size()); // 标记第2维长度
            value.setInits2(symbolCon.getInitval2()); // 将2维数组的初始值传入
            symbolCon.setValue(value);
            /* 生成IrAlloca指令 */
            IrAlloca irAlloca = new IrAlloca(type, value);
            irAlloca.setDimension(2); // 设置维度
            irAlloca.setDimension1(value.getDimension1()); // 设置1维变量
            irAlloca.setDimension2(value.getDimension2()); // 设置2维变量
            irAlloca.setName(name);
            this.instructions.add(irAlloca);
            int i = 0;
            ArrayList<ArrayList<Integer>> inits = symbolCon.getInitval2();
            for (ArrayList<Integer> index1 : inits) {
                if (index1 == null || index1.size() == 0) {
                    i += 1;
                    continue;
                }
                int j = 0;
                for (Integer index2 : index1) {
                    /* 为常数构造IrValue */
                    IrValue val = new IrValue(IrIntegerType.get32(), String.valueOf(index2));
                    IrStore store = new IrStore(val, value, 0, 2, -1, i, -1, j);
                    this.instructions.add(store);
                    j += 1;
                }
                i += 1;
            }
        } else {
            System.out.println(
                    "ERROR in IrInstructionBuilder.addCon : should not reach here");
        }
    }

    /**
     * 添加符号初值，为addCon服务
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
            for (ConstInitVal initVal1 : initValMulti.getAllConstInitVals()) {
                temp.add(initVal1.calcNode(this.symbolTable));
            }
            symbolCon.setInitval1(temp);
        } else if (dimension == 2) { // 二维数组初值
            ConstInitValMulti initValMulti = (ConstInitValMulti)initVal.getConstInitValEle();
            ArrayList<ArrayList<Integer>> temp1 = new ArrayList<>();
            for (ConstInitVal initVal1 : initValMulti.getAllConstInitVals()) {
                // initVal1 类型应当是一维数组
                ConstInitValMulti initValMulti1 = (ConstInitValMulti)initVal1.getConstInitValEle();
                ArrayList<Integer> temp2 = new ArrayList<>();
                for (ConstInitVal initVal2 : initValMulti1.getAllConstInitVals()) {
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
        VarDef first = this.varDecl.getFirst();
        addVar(first);
        ArrayList<VarDef> varDefs = this.varDecl.getVarDefs();
        if (varDefs != null && varDefs.size() != 0) {
            for (VarDef varDef : varDefs) {
                addVar(varDef);
            }
        }
    }

    private void addVar(VarDef varDef) {
        VarDefEle varDefEle = varDef.getVarDefEle();
        if (varDefEle instanceof VarDefNull) {
            // 无初值变量声明
            VarDefNull varDefNull = (VarDefNull)varDefEle;
            int cnt = this.functionCnt.getCnt();
            String name = "%_LocalVariable" + cnt; // 中间代码中的名字
            int dimension = varDefNull.getDimension();
            IrValueType type = IrIntegerType.get32();
            if (dimension == 0) {
                // 0维
                // 生成IrValue对象
                IrValue irValue = new IrValue(type);
                irValue.setName(name);
                // 生成SymbolVar对象
                SymbolType symbolType = SymbolType.VAR;
                SymbolVar symbolVar = new SymbolVar(varDefNull.getName(),
                        symbolType, dimension, irValue);
                addVarSymbol(symbolVar);
                IrAlloca irAlloca = new IrAlloca(type, irValue);
                /* 标记Alloca的维数 */
                irAlloca.setDimension(0);
                irAlloca.setName(name);
                this.instructions.add(irAlloca);
            } else if (dimension == 1) {
                // 1维变量数组
                // 生成IrValue对象
                IrValue irValue = new IrValue(type);
                // 标记维数
                irValue.setDimension(dimension);
                irValue.setName(name);
                // 标记一维大小
                irValue.setDimension1(varDefNull.getDimension1(this.symbolTable));
                // 生成SymbolVar对象
                SymbolType symbolType = SymbolType.VAR1;
                SymbolVar symbolVar = new SymbolVar(varDefNull.getName(),
                        symbolType, dimension, irValue);
                addVarSymbol(symbolVar);
                IrAlloca irAlloca = new IrAlloca(type, irValue);
                /* 标记Alloca申请的维数 */
                irAlloca.setDimension(1);
                /* 标记申请的维数 */
                irAlloca.setDimension1(irValue.getDimension1());
                irAlloca.setName(name);
                this.instructions.add(irAlloca);
            } else if (dimension == 2) {
                // 2维变量数组
                IrValue irValue = new IrValue(type);
                // 标记维数
                irValue.setDimension(dimension);
                irValue.setName(name);
                // 标记一维大小
                irValue.setDimension1(varDefNull.getDimension1(this.symbolTable));
                // 标记二维大小
                irValue.setDimension2(varDefNull.getDimension2(this.symbolTable));
                // 生成SymbolVar对象
                SymbolType symbolType = SymbolType.VAR2;
                SymbolVar symbolVar = new SymbolVar(varDefNull.getName(),
                        symbolType, dimension, irValue);
                addVarSymbol(symbolVar);
                IrAlloca irAlloca = new IrAlloca(type, irValue);
                /* 标记Alloca维数 */
                irAlloca.setDimension(dimension);
                irAlloca.setDimension1(irValue.getDimension1());
                irAlloca.setDimension2(irValue.getDimension2());
                irAlloca.setName(name);
                this.instructions.add(irAlloca);
            } else {
                System.out.println("ERROR in IrInstructionBuilder.addVar : should not reach here");
            }

        } else if (varDefEle instanceof VarDefInit) {
            // 有初值常量声明
            VarDefInit varDefInit = (VarDefInit)varDefEle;
            int cnt = this.functionCnt.getCnt();
            String name = "%_LocalVariable" + cnt; // 中间代码中的名字
            int dimension = varDefInit.getDimension();
            if (dimension == 0) {
                genIrInstructionFromVarDefInit0(varDefInit, name, dimension);
            } else if (dimension == 1) {
                // 1维
                genIrInstructionFromVarDefInit1(varDefInit, name, dimension);
            } else if (dimension == 2) {
                // 2维
                genIrInstructionFromVarDefInit2(varDefInit, name, dimension);
            } else {
                System.out.println("ERROR in IrInstructionBuilder.addVar : should not reach here");
            }
        } else {
            System.out.println("ERROR in IrInstructionBuilder.addVar : should not reach here");
        }
    }

    /* 从0维VarDefInit生成中间代码 */
    private void genIrInstructionFromVarDefInit0(VarDefInit varDefInit,
                                                 String name,
                                                 int dimension) {
        IrValue irValue = new IrValue(IrIntegerType.get32());
        irValue.setName(name);
        /* 生成SymbolVar对象并加入符号表 */
        SymbolType symbolType = SymbolType.VAR;
        SymbolVar symbolVar = new SymbolVar(varDefInit.getName(),
                symbolType, dimension, irValue);
        addVarSymbol(symbolVar);
        /* 生成内存申请指令 */
        IrAlloca irAlloca = new IrAlloca(IrIntegerType.get32(), irValue);
        /* 标记Alloca维数 */
        irAlloca.setDimension(0);
        irAlloca.setName(name);
        this.instructions.add(irAlloca);
        InitVal initVal = varDefInit.getInitVal();
        InitValEle initValEle = initVal.getInitValEle();
        if (!(initValEle instanceof Exp)) {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        } else {
            Exp exp = (Exp)initValEle;
            IrValue right = genIrInstructionFromExp(exp, false);
            IrStore store = new IrStore(right, irAlloca);
            /* 生成赋值指令 */
            this.instructions.add(store);
        }
    }

    /* 从1维数组VarDefInit生成中间代码 */
    private void genIrInstructionFromVarDefInit1(VarDefInit varDefInit,
                                                 String name,
                                                 int dimension) {
        IrValue irValue = new IrValue(IrIntegerType.get32());
        // 标记维数
        irValue.setDimension(1);
        irValue.setDimension1(varDefInit.getDimension1(this.symbolTable));
        irValue.setName(name);
        /* 生成SymbolVar对象并加入符号表 */
        SymbolType symbolType = SymbolType.VAR1;
        SymbolVar symbolVar = new SymbolVar(varDefInit.getName(),
                symbolType, dimension, irValue);
        addVarSymbol(symbolVar);
        /* 生成内存申请指令 */
        IrAlloca irAlloca = new IrAlloca(IrIntegerType.get32(), irValue);
        // 标记是1维数组
        irAlloca.setDimension(1);
        irAlloca.setDimension1(irValue.getDimension1());
        irAlloca.setName(name);
        this.instructions.add(irAlloca);
        InitVal initVal = varDefInit.getInitVal();
        /* InitValEle应该是InitVals类型 */
        InitValEle initValEle = initVal.getInitValEle();
        /* 每个InitVal都应该是Exp类型的 */
        ArrayList<InitVal> initVals = ((InitVals)initValEle).getAllInitVals();
        /* 标记当前是数组的多少位偏移 */
        int cnt = 0;
        for (InitVal initVal1 : initVals) {
            InitValEle index = initVal1.getInitValEle();
            if (!(index instanceof Exp)) {
                System.out.println("ERROR in IrInstructionBuilder : should not reach here");
            } else {
                Exp exp = (Exp)index;
                IrValue right = genIrInstructionFromExp(exp, false);
                IrStore store = new IrStore(right, irAlloca, right.getDimension(),
                        irAlloca.getDimension(), right.getDimension1(), cnt, 0, 0);
                this.instructions.add(store);
            }
            cnt += 1;
        }
    }

    /* 从2维VarDefInit生成中间代码 */
    private void genIrInstructionFromVarDefInit2(VarDefInit varDefInit,
                                                 String name,
                                                 int dimension) {
        IrValue irValue = new IrValue(IrIntegerType.get32());
        // 标记维数
        irValue.setDimension(2);
        irValue.setDimension1(varDefInit.getDimension1(this.symbolTable));
        irValue.setDimension2(varDefInit.getDimension2(this.symbolTable));
        irValue.setName(name);
        /* 生成SymbolVar对象并加入符号表 */
        SymbolType symbolType = SymbolType.VAR2;
        SymbolVar symbolVar = new SymbolVar(varDefInit.getName(),
                symbolType, dimension, irValue);
        addVarSymbol(symbolVar);
        /* 生成内存申请指令 */
        IrAlloca irAlloca = new IrAlloca(IrIntegerType.get32(), irValue);
        // 标记是2维数组
        irAlloca.setDimension(2);
        irAlloca.setDimension1(irValue.getDimension1());
        irAlloca.setDimension2(irValue.getDimension2());
        irAlloca.setName(name);
        this.instructions.add(irAlloca);
        InitVal initVal = varDefInit.getInitVal();
        /* InitValEle应该是InitVals类型 */
        InitValEle initValEle = initVal.getInitValEle();
        /* 每个InitVal都应该是InitVals类型的，再往下一层才是Exp */
        ArrayList<InitVal> initVals = ((InitVals)initValEle).getAllInitVals();
        /* 标记当前是数组的多少位偏移 */
        int i = 0;
        for (InitVal initVal1 : initVals) {
            InitValEle indexI = initVal1.getInitValEle();
            if (!(indexI instanceof InitVals)) {
                System.out.println("ERROR in IrInstructionBuilder : should not reach here");
            } else {
                InitVals initVals1 = (InitVals)indexI;
                ArrayList<InitVal> initVals2 = initVals1.getAllInitVals();
                int j = 0;
                for (InitVal initVal2 : initVals2) {
                    InitValEle indexJ = initVal2.getInitValEle();
                    if (!(indexJ instanceof Exp)) {
                        System.out.println("ERROR in IrInstructionBuilder : should not reach here");
                    } else {
                        Exp exp = (Exp)indexJ;
                        IrValue right = genIrInstructionFromExp(exp, false);
                        IrStore store = new IrStore(right, irAlloca, right.getDimension(),
                                irAlloca.getDimension(), right.getDimension1(), i,
                                right.getDimension2(), j);
                        this.instructions.add(store);
                    }
                    j += 1;
                }
            }
            i += 1;
        }
    }

    /**
     * 为0维VarDef生成赋值语句（组）
     * 由于可能生成不止一条指令，因此可能会产生多条指令，其左值为我们设的临时变量
     * 由于这些临时变量是用完即弃的，所以不会被填入符号表，其传递形式为通过方法返回值
     */

    private IrValue genIrInstructionFromExp(Exp exp, boolean isLeft) {
        return genIrInstructionFromAddExp(exp.getAddExp(), isLeft);
    }

    /* 传入表达式右值中的一个AddExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromAddExp(AddExp addExp, boolean isLeft) {
        IrValue ret = null;
        MulExp first = addExp.getFirst();
        IrValue left = genIrInstructionFromMulExp(first, isLeft);
        if (addExp.getOperands().size() == 0) {
            // AddExp -> MulExp
            ret = left;
        } else {
            ArrayList<MulExp> operands = addExp.getOperands();
            ArrayList<Token> operators = addExp.getOperators();
            IrValue right = null;
            int len = operands.size();
            IrValueType valueType = IrIntegerType.get32();
            for (int i = 0; i < len; i++) {
                MulExp mulExp = operands.get(i);
                Token operator = operators.get(i);
                right = genIrInstructionFromMulExp(mulExp, isLeft);
                IrBinaryInst addInst;
                if (operator.getType().equals(TokenType.PLUS)) {
                    addInst = new IrBinaryInst(valueType, IrInstructionType.Add, left, right);
                } else if (operator.getType().equals(TokenType.MINU)) {
                    addInst = new IrBinaryInst(valueType, IrInstructionType.Sub, left, right);
                } else {
                    System.out.println("ERROR in IrInstructionBuilder : should not reach here");
                    addInst = null;
                }
                int cnt = functionCnt.getCnt();
                String addInstName = "%_LocalVariable" + cnt;
                addInst.setName(addInstName);
                this.instructions.add(addInst);
                left = addInst;
            }
            ret = left;
        }
        this.left = ret;
        return ret;
    }

    public IrValue getLeft() {
        return left;
    }

    /* 传入表达式右值中的一个MulExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromMulExp(MulExp mulExp, boolean isLeft) {
        IrValue ret = null;
        UnaryExp first = mulExp.getFirst();
        IrValue left = genIrInstructionFromUnaryExp(first, isLeft);
        if (mulExp.getOperands().size() == 0) {
            // MulExp -> UnaryExp
            ret = left;
        } else {
            ArrayList<UnaryExp> operands = mulExp.getOperands();
            ArrayList<Token> operators = mulExp.getOperators();
            IrValue right = null;
            int len = operands.size();
            IrValueType valueType = IrIntegerType.get32();
            for (int i = 0; i < len; i++) {
                UnaryExp unaryExp = operands.get(i);
                Token operator = operators.get(i);
                right = genIrInstructionFromUnaryExp(unaryExp, isLeft);
                IrBinaryInst mulInst;
                if (operator.getType().equals(TokenType.MULT)) {
                    mulInst = new IrBinaryInst(valueType, IrInstructionType.Mul, left, right);
                } else if (operator.getType().equals(TokenType.DIV)) {
                    mulInst = new IrBinaryInst(valueType, IrInstructionType.Div, left, right);
                } else if (operator.getType().equals(TokenType.MOD)) {
                    mulInst = new IrBinaryInst(valueType, IrInstructionType.Mod, left, right);
                } else {
                    System.out.println("ERROR in IrInstructionBuilder : should not reach here");
                    mulInst = null;
                }
                int cnt = functionCnt.getCnt();
                String addInstName = "%_LocalVariable" + cnt;
                mulInst.setName(addInstName);
                this.instructions.add(mulInst);
                left = mulInst;
            }
            ret = left;
        }
        return ret;
    }

    /* 传入表达式右值中的一个UnaryExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromUnaryExp(UnaryExp unaryExp, boolean isLeft) {
        IrValue ret = null;
        UnaryExpEle expEle = unaryExp.getUnaryExpEle();
        if (expEle instanceof PrimaryExp) {
            // PrimaryExp基本表达式
            PrimaryExp primaryExp = (PrimaryExp)expEle;
            ret = genIrInstructionFromPrimaryExp(primaryExp, isLeft);
        } else if (expEle instanceof UnaryExpFunc) {
            // UnaryExpFunc调用函数
            UnaryExpFunc unaryExpFunc = (UnaryExpFunc)expEle;
            ret = genIrInstructionFromUnaryExpFunc(unaryExpFunc, isLeft);
        } else if (expEle instanceof UnaryExpOp) {
            // '+' / '-' / '!' UnaryExp
            // '!'只可能出现在Cond中
            UnaryExpOp unaryExpOp = (UnaryExpOp)expEle;
            ret = genIrInstructionFromUnaryExpOp(unaryExpOp, isLeft);
        } else {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        }
        return ret;
    }

    /* 传入表达式右值中的一个PrimaryExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromPrimaryExp(PrimaryExp primaryExp, boolean isLeft) {
        PrimaryExpEle expEle = primaryExp.getPrimaryExpEle();
        IrValue ret = null;
        if (expEle instanceof LVal) {
            LVal lval = (LVal)expEle;
            // ret = genIrInstructionFromLVal(lval, false);
            ret = genIrInstructionFromLVal(lval, isLeft);
        } else if (expEle instanceof Number) {
            Number number = (Number)expEle;
            ret = genIrInstructionFromNumber(number);
        } else if (expEle instanceof PrimaryExpExp) {
            PrimaryExpExp exp = (PrimaryExpExp)expEle;
            ret = genIrInstructionFromPrimaryExpExp(exp, isLeft);
        }
        return ret;
    }

    /* 传入表达式中的一个LVal，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromLVal(LVal lval, boolean isLeft) {
        IrValue ret = null;
        String nameSysy = lval.getName();
        // 查询符号的维数
        int dimension = lval.getValueDimension();
        // int dimension = lval.getDimension();
        if (dimension == 0) {
            Symbol symbol = this.symbolTable.getSymbol(nameSysy);
            if (!(symbol instanceof SymbolCon || symbol instanceof SymbolVar)) {
                System.out.println("ERROR in IrInstructionBuilder : should not reach here");
            } else {
                /* 和Symbol存在一起的局部变量是指针，拿来使用需要生成load语句 */
                if (isLeft) {
                    // 左值，说明应当直接取用
                    ret = symbol.getValue();
                } else {
                    // 非左值
                    // 局部常量，直接变成数字
                    IrValue ptr = symbol.getValue();
                    if (!(ptr.getName().contains("%") || ptr.getName().contains("@"))) {
                        return ptr;
                    }
                    // 函数形参，直接返回
                    if (ptr.isParam()) {
                        return ptr;
                    }

                    // 局部变量，需要从内存中读取
                    IrValueType type = IrIntegerType.get32(); // 语句的类型是32位
                    int cnt = this.functionCnt.getCnt();
                    String retName = "%_LocalVariable" + cnt;
                    IrLoad irLoad = new IrLoad(type, ptr);
                    irLoad.setName(retName);
                    this.instructions.add(irLoad);
                    return irLoad;
                }
            }
        } else if (dimension == 1) {
            /* 1维数组 */
            Symbol symbol = this.symbolTable.getSymbol(nameSysy);
            // 数组维度变量
            ArrayList<Exp> exps = lval.getExps();
            if (isLeft) {
                // 是左值，说明应当直接取用
                // ret = symbol.getValue();
                ret = symbol.getValue().cloneForCall();
                ArrayList<Exp> lvalExps = lval.getExps();
                /* 对int a[10]; */
                if (lvalExps == null || lvalExps.size() == 0) {
                    /* 形如a */
                    ret.setDimensionValue(1);
                } else if (lvalExps.size() == 1) {
                    /* 形如a[1] */
                    ret.setDimensionValue(0);
                    ret.setDimension1Value(genIrInstructionFromExp(exps.get(0), true));
                } else {
                    System.out.println(
                            "ERROR in IrInstructionBuilder.genLval : should not reach here");
                }

            } else {
                // 1维数组对象
                IrValue ptr = symbol.getValue();
                IrValueType type = IrIntegerType.get32();
                // 解析并获取1维度变量
                Exp exp = exps.get(0);
                IrValue dimension1 = genIrInstructionFromExp(exp, isLeft);
                // 为储存从内存加载的数组元素的临时变量命名
                int cnt = this.functionCnt.getCnt();
                String retName = "%_LocalVariable" + cnt;
                IrLoad irLoad = new IrLoad(type, ptr, dimension, dimension1, null);
                irLoad.setName(retName);
                this.instructions.add(irLoad);
                ret = irLoad;
            }
        } else if (dimension == 2) {
            /* 2维数组 */
            /* TODO : 检查传递二维数组的某一个一维数组 */
            Symbol symbol = this.symbolTable.getSymbol(nameSysy);
            ArrayList<Exp> exps = lval.getExps();
            if (isLeft) {
                ArrayList<Exp> lvalExps = lval.getExps();
                // ret = symbol.getValue();
                ret = symbol.getValue().cloneForCall();
                /* 对int a[2][3] */
                if (lvalExps == null || lvalExps.size() == 0) {
                    /* 形如a */
                    ret.setDimensionValue(2);
                } else if (lvalExps.size() == 1) {
                    /* 调用了二维数组的一维切片 */
                    /* 形如a[1] */
                    ret.setDimensionValue(1);
                    ret.setDimension1Value(genIrInstructionFromExp(exps.get(0), true));
                } else if (lvalExps.size() == 2) {
                    /* 形如a[1][2] */
                    ret.setDimensionValue(0);
                    ret.setDimension1Value(genIrInstructionFromExp(exps.get(0), true));
                    ret.setDimension2Value(genIrInstructionFromExp(exps.get(1), true));
                } else {
                    System.out.println(
                            "ERROR in IrInstructionBuilder.genLval : should not reach here");
                }
            } else {
                // 2维数组对象
                IrValue ptr = symbol.getValue();
                IrValueType type = IrIntegerType.get32();
                // 解析并获取1,2维度变量
                IrValue dimension1 = genIrInstructionFromExp(exps.get(0), isLeft);
                IrValue dimension2 = genIrInstructionFromExp(exps.get(1), isLeft);
                // 为储存从内存加载的数组元素的临时变量命名
                int cnt = this.functionCnt.getCnt();
                String retName = "%_LocalVariable" + cnt;
                IrLoad irLoad = new IrLoad(type, ptr, dimension, dimension1, dimension2);
                irLoad.setName(retName);
                this.instructions.add(irLoad);
                ret = irLoad;
            }
        } else {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        }
        return ret;
    }

    /* 传入表达式右值中的一个Number，返回一个IrValue以供调用 */
    public IrValue genIrInstructionFromNumber(Number number) {
        int num = number.calcNode(this.symbolTable);
        IrValue ret = new IrValue(IrIntegerType.get32(), String.valueOf(num));
        return ret;
    }

    /* 传入表达式右值中的一个PrimaryExpExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromPrimaryExpExp(PrimaryExpExp primaryExpExp, boolean isLeft) {
        Exp exp = primaryExpExp.getExp();
        return genIrInstructionFromExp(exp, isLeft);
    }

    /* 传入表达式右值中的一个UnaryExpFunc，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromUnaryExpFunc(UnaryExpFunc func, boolean isLeft) {
        /* TODO : 需要处理参数是数组的情况 */
        IrCall call = null;
        String functionName = func.getFunctionName(); // 函数名
        FuncRParams funcRParams = func.getFuncRParams();
        ArrayList<IrValue> args = new ArrayList<>();
        Symbol symbol = this.symbolTable.getSymbol(functionName);
        IrFunction irFunction = (IrFunction)symbol.getValue();
        if (funcRParams == null) {
            // 说明无参数
            call = new IrCall(irFunction, args);
        } else {
            SymbolFunc symbolFunc = (SymbolFunc)symbol; 
            ArrayList<Symbol> symbols = symbolFunc.getSymbols();
            ArrayList<Exp> exps = funcRParams.getAllExps();
            IrValue arg = null;
            int i = 0;
            for (Exp exp : exps) {
                Symbol symbolNowExp = symbols.get(i);
                int dimension = symbolNowExp.getDimension();
                if (dimension != 0) {
                    /* 对待参数中的数组部分 */
                    arg = genIrInstructionFromExp(exp, true);
                    args.add(arg);
                } else {
                    /* 对待参数中的数组部分 */
                    arg = genIrInstructionFromExp(exp, false);
                    IrValue left = new IrValue(IrIntegerType.get32());
                    int cnt = this.functionCnt.getCnt();
                    String name = "%_LocalVariable" + cnt;
                    IrAlloca irAlloca = new IrAlloca(IrIntegerType.get32(), left);
                    irAlloca.setName(name);
                    this.instructions.add(irAlloca);
                    IrStore store = new IrStore(arg, left);
                    left.setName(name);
                    store.setName(name);
                    instructions.add(store);
                    args.add(store);
                }
                
                i += 1;
            }
            // Exp first = funcRParams.getFirst();
            // IrValue arg = genIrInstructionFromExp(first, true);
            // IrValue arg = genIrInstructionFromExp(first, false);
            /*
            ArrayList<Exp> exps = funcRParams.getExps();
            for (Exp exp : exps) {
                arg = genIrInstructionFromExp(exp, true);
                args.add(arg);
            }*/
            call = new IrCall(irFunction, args);
        }
        if (!call.getVoid()) {
            int cnt = this.functionCnt.getCnt();
            String name = "%_LocalVariable" + cnt;
            call.setName(name);
        }
        this.instructions.add(call);
        return call;
    }

    /* 传入表达式右值中的一个UnaryExpOp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromUnaryExpOp(UnaryExpOp expOp, boolean isLeft) {
        IrValue ret = null;
        UnaryOp unaryOp = expOp.getUnaryOp();
        UnaryExp unaryExp = expOp.getUnaryExp();
        Token op = unaryOp.getToken();
        if (op.getType().equals(TokenType.PLUS)) {
            ret = genIrInstructionFromUnaryExp(unaryExp, isLeft);
        } else if (op.getType().equals(TokenType.MINU)) {
            /* TODO : 这里处理的是i32，暂时没有考虑别的情况 */
            IrValue left = new IrValue(IrIntegerType.get32(), "-1");
            IrBinaryInst mulExp = new IrBinaryInst(IrIntegerType.get32(),
                    IrInstructionType.Mul, left, genIrInstructionFromUnaryExp(unaryExp, isLeft));
            int cnt = this.functionCnt.getCnt();
            String name = "%_LocalVariable" + cnt;
            mulExp.setName(name);
            ret = mulExp;
            this.instructions.add(mulExp);
        } else if (op.getType().equals(TokenType.NOT)) {
            /* 处理Not运算 */
            IrBinaryInst notExp = new IrBinaryInst(IrIntegerType.get32(), IrInstructionType.Not,
                    genIrInstructionFromUnaryExp(unaryExp, isLeft), null);
            int cnt = this.functionCnt.getCnt();
            String name = "%_LocalVariable" + cnt;
            notExp.setName(name);
            ret = notExp;
            this.instructions.add(notExp);
        } else {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        }
        return ret;
    }

    private void addVarSymbol(SymbolVar symbolVar) {
        this.symbolTable.addSymol(symbolVar);
    }

    /* 需要考虑左右分别是0,1,2维数组的情况 */
    private void genIrInstructionFromStmtAssign() {
        LVal lval = stmtAssign.getLval();
        Exp exp = stmtAssign.getExp();
        IrValue right = genIrInstructionFromExp(exp, false);
        IrValue left = genIrInstructionFromLVal(lval, true);
        /* left := right */
        int leftDimension = left.getDimension();
        IrStore store = null;
        /* 无需考虑right的维数，必然为0，因为这是gen...Exp函数的返回值约定 */
        int rightDimension = right.getDimension();
        if (leftDimension == 0) {
            /* 0维变量 */
            store = new IrStore(right, left);
        } else {
            /* 涉及数组，以下为排列组合 */
            IrValue dimension1PointerValue = null;
            IrValue dimension2PointerValue = null;
            if (leftDimension == 1) {
                /* 1维数组 */
                ArrayList<Exp> exps = lval.getExps();
                dimension1PointerValue = genIrInstructionFromExp(exps.get(0), false);
                // store = new IrStore(right, left, 0, 1, null, dimension1, null, null);
            } else if (leftDimension == 2) {
                /* 2维数组 */
                ArrayList<Exp> exps = lval.getExps();
                dimension1PointerValue = genIrInstructionFromExp(exps.get(0), false);
                dimension2PointerValue = genIrInstructionFromExp(exps.get(1), false);
            } else {
                System.out.println("ERROR IN IrInstructionBuilder : should not reach here");
            }
            store = new IrStore(right, left, rightDimension, leftDimension,
                    null, dimension1PointerValue, 
                    null, dimension2PointerValue);
        }
        this.instructions.add(store);
    }

    private void genIrInstructionFromStmtContinue() {
        /* 循环语句块continue */
        IrGoto irGoto = new IrGoto(this.whileLabel);
        this.instructions.add(irGoto);
    }

    private void genIrInstructionFromStmtBreak() {
        /* 循环语句块break */
        IrGoto irGoto = new IrGoto(this.endLabel);
        this.instructions.add(irGoto);
    }

    /* 解析return语句 */
    private void genIrInstructionFromStmtReturn() {
        IrRet ret;
        if (this.stmtReturn.hasExp()) {
            Exp exp = this.stmtReturn.getExp();
            IrValue retVar = genIrInstructionFromExp(exp, false);
            ret = new IrRet(retVar);
        } else {
            ret = new IrRet();
        }
        this.instructions.add(ret);
    }

    /* 解析输入语句 */
    private void genIrInstructionFromStmtGetint() {
        LVal lval = this.stmtGetint.getLval();
        IrValue left = genIrInstructionFromLVal(lval, true);
        IrCall irCall = new IrCall("@getint");
        int cnt = this.functionCnt.getCnt();
        String name = "%_LocalVariable" + cnt;
        // 使用临时变量保存从标准输入流获取的输入
        irCall.setName(name);
        this.instructions.add(irCall);
        // 将输入存回
        /* left := right */
        IrStore store = null;
        if (left.getDimension() == 0) {
            store = new IrStore(irCall, left);
        } else if (left.getDimension() == 1) {
            /* 1维数组 */
            ArrayList<Exp> exps = lval.getExps();
            IrValue dimension1 = genIrInstructionFromExp(exps.get(0), false);
            store = new IrStore(irCall, left, 0, 1, null, dimension1, null, null);
        } else if (left.getDimension() == 2) {
            /* 2维数组 */
            ArrayList<Exp> exps = lval.getExps();
            IrValue dimension1 = genIrInstructionFromExp(exps.get(0), false);
            IrValue dimension2 = genIrInstructionFromExp(exps.get(1), false);
            store = new IrStore(irCall, left, 0, 2, null, dimension1, null, dimension2);
        } else {
            System.out.println("ERROR IN IrInstructionBuilder : should not reach here");
        }
        this.instructions.add(store);
    }

    /* 解析输出语句 */
    private void genIrInstructionFromStmtPrint() {
        FormatString formatString = this.stmtPrint.getFormatString();
        ArrayList<Exp> exps = this.stmtPrint.getExps();
        Token tokenString = formatString.getToken();
        String string = tokenString.getContent();
        char[] chars = string.substring(1, string.length() - 1).toCharArray();
        int len = chars.length;
        int cnt = 0;

        ArrayList<IrValue> values = new ArrayList<>();
        /* 首先将exps处理出来 */
        int expSize = exps.size();
        for (int i = 0; i < expSize; i++) {
            values.add(genIrInstructionFromExp(exps.get(i), false));
        }
        for (int i = 0; i < len; i++) {
            char c = chars[i];
            IrCall irCall;
            if (c == '%') {
                // IrValue value = genIrInstructionFromExp(exps.get(cnt));
                IrValue value = values.get(cnt);
                irCall = new IrCall("@putint", value);
                cnt += 1;
                i += 1;
            } else if (c == '\\') {
                irCall = new IrCall("@putch", '\n');
                i += 1;
            } else {
                irCall = new IrCall("@putch", c);
            }
            this.instructions.add(irCall);
        }
    }

    private void genIrInstructionFromStmtExp() {
        Exp exp = this.stmtExp.getExp();
        genIrInstructionFromExp(exp, false);
    }

}
