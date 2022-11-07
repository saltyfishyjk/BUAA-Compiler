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
import middle.llvmir.value.instructions.terminator.IrRet;
import middle.symbol.Symbol;
import middle.symbol.SymbolCon;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;
import middle.symbol.SymbolVar;

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
                                IrBasicBlock basicBlock,
                                IrFunctionCnt functionCnt) {
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
        SymbolCon symbolCon;
        if (constDef.getDimension() == 0) {
            // 数值常数
            SymbolType symbolType = SymbolType.CON;
            symbolCon = new SymbolCon(constDef.getName(), symbolType, 0);
            this.symbolTable.addSymol(symbolCon);
            setInitVal(symbolCon, constDef.getConstInitval());
            IrValue value = new IrValue(IrIntegerType.get32(),
                    String.valueOf(symbolCon.getInitVal()));
            symbolCon.setValue(value);
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
        VarDef first = this.varDecl.getFirst();
        addVar(first);
        ArrayList<VarDef> varDefs = this.varDecl.getVarDefs();
        if (varDefs != null && varDefs.size() != 0) {
            for (VarDef varDef : varDefs) {
                addVar(varDef);
            }
        }
        /* TODO : 待施工 */
    }

    private void addVar(VarDef varDef) {
        VarDefEle varDefEle = varDef.getVarDefEle();
        if (varDefEle instanceof VarDefNull) {
            // 无初值变量声明
            VarDefNull varDefNull = (VarDefNull)varDefEle;
            int cnt = this.functionCnt.getCnt();
            String name = "%_LocalVariable" + cnt; // 中间代码中的名字
            int dimension = varDefNull.getDimension();
            if (dimension == 0) {
                // 零维
                // 生成IrValue对象
                IrValue irValue = new IrValue(IrIntegerType.get32());
                irValue.setName(name);
                // 生成SymbolVar对象
                SymbolType symbolType = SymbolType.VAR;
                SymbolVar symbolVar = new SymbolVar(varDefNull.getName(),
                        symbolType, dimension, irValue);
                addVarSymbol(symbolVar);
                IrAlloca irAlloca = new IrAlloca(IrIntegerType.get32(), irValue);
                irAlloca.setName(name);
                this.instructions.add(irAlloca);
            } else if (dimension == 1) {
                // 一维
                /* TODO : 本次作业不涉及数组 */
            } else if (dimension == 2) {
                // 二维
                /* TODO : 本次作业不涉及数组 */
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
                // 一维
                /* TODO : 本次作业不涉及数组 */
            } else if (dimension == 2) {
                // 二维
                /* TODO : 本次作业不涉及数组 */
            } else {
                System.out.println("ERROR in IrInstructionBuilder.addVar : should not reach here");
            }
        } else {
            System.out.println("ERROR in IrInstructionBuilder.addVar : should not reach here");
        }
        /* TODO : 待施工 */
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
        irAlloca.setName(name);
        this.instructions.add(irAlloca);
        InitVal initVal = varDefInit.getInitVal();
        InitValEle initValEle = initVal.getInitValEle();
        if (!(initValEle instanceof Exp)) {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        } else {
            Exp exp = (Exp)initValEle;
            IrValue right = genIrInstructionFromExp(exp);
            IrStore store = new IrStore(right, irAlloca);
            /* 生成赋值指令 */
            this.instructions.add(store);
        }
    }

    /**
     * 为0维VarDef生成赋值语句（组）
     * 由于可能生成不止一条指令，因此可能会产生多条指令，其左值为我们设的临时变量
     * 由于这些临时变量是用完即弃的，所以不会被填入符号表，其传递形式为通过方法返回值
     */

    private IrValue genIrInstructionFromExp(Exp exp) {
        return genIrInstructionFromAddExp(exp.getAddExp());
    }

    /* 传入表达式右值中的一个AddExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromAddExp(AddExp addExp) {
        IrValue ret = null;
        MulExp first = addExp.getFirst();
        IrValue left = genIrInstructionFromMulExp(first);
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
                right = genIrInstructionFromMulExp(mulExp);
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
        return ret;
    }

    /* 传入表达式右值中的一个MulExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromMulExp(MulExp mulExp) {
        IrValue ret = null;
        UnaryExp first = mulExp.getFirst();
        IrValue left = genIrInstructionFromUnaryExp(first);
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
                right = genIrInstructionFromUnaryExp(unaryExp);
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
    private IrValue genIrInstructionFromUnaryExp(UnaryExp unaryExp) {
        IrValue ret = null;
        UnaryExpEle expEle = unaryExp.getUnaryExpEle();
        if (expEle instanceof PrimaryExp) {
            // PrimaryExp基本表达式
            PrimaryExp primaryExp = (PrimaryExp)expEle;
            ret = genIrInstructionFromPrimaryExp(primaryExp);
        } else if (expEle instanceof UnaryExpFunc) {
            // UnaryExpFunc调用函数
            UnaryExpFunc unaryExpFunc = (UnaryExpFunc)expEle;
            ret = genIrInstructionFromUnaryExpFunc(unaryExpFunc);
        } else if (expEle instanceof UnaryExpOp) {
            // '+' / '-' UnaryExp
            UnaryExpOp unaryExpOp = (UnaryExpOp)expEle;
            ret = genIrInstructionFromUnaryExpOp(unaryExpOp);
        } else {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        }
        return ret;
    }

    /* 传入表达式右值中的一个PrimaryExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromPrimaryExp(PrimaryExp primaryExp) {
        PrimaryExpEle expEle = primaryExp.getPrimaryExpEle();
        IrValue ret = null;
        if (expEle instanceof LVal) {
            LVal lval = (LVal)expEle;
            ret = genIrInstructionFromLVal(lval, false);
        } else if (expEle instanceof Number) {
            Number number = (Number)expEle;
            ret = genIrInstructionFromNumber(number);
        } else if (expEle instanceof PrimaryExpExp) {
            PrimaryExpExp exp = (PrimaryExpExp)expEle;
            ret = genIrInstructionFromPrimaryExpExp(exp);
        }
        return ret;
    }

    /* 传入表达式中的一个LVal，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromLVal(LVal lval, boolean isLeft) {
        IrValue ret = null;
        String nameSysy = lval.getName();
        int dimension = lval.getDimension();
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
            /* TODO : 本次作业不涉及数组 */
        } else if (dimension == 2) {
            /* TODO : 本次作业不涉及数组 */
        } else {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        }
        return ret;
    }

    /* 传入表达式右值中的一个Number，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromNumber(Number number) {
        int num = number.calcNode(this.symbolTable);
        IrValue ret = new IrValue(IrIntegerType.get32(), String.valueOf(num));
        return ret;
    }

    /* 传入表达式右值中的一个PrimaryExpExp，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromPrimaryExpExp(PrimaryExpExp primaryExpExp) {
        Exp exp = primaryExpExp.getExp();
        return genIrInstructionFromExp(exp);
    }

    /* 传入表达式右值中的一个UnaryExpFunc，返回一个IrValue以供调用 */
    private IrValue genIrInstructionFromUnaryExpFunc(UnaryExpFunc func) {
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
            Exp first = funcRParams.getFirst();
            args.add(genIrInstructionFromExp(first));
            ArrayList<Exp> exps = funcRParams.getExps();
            for (Exp exp : exps) {
                args.add(genIrInstructionFromExp(exp));
            }
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
    private IrValue genIrInstructionFromUnaryExpOp(UnaryExpOp expOp) {
        IrValue ret = null;
        UnaryOp unaryOp = expOp.getUnaryOp();
        UnaryExp unaryExp = expOp.getUnaryExp();
        Token op = unaryOp.getToken();
        if (op.getType().equals(TokenType.PLUS)) {
            return genIrInstructionFromUnaryExp(unaryExp);
        } else if (op.getType().equals(TokenType.MINU)) {
            /* TODO : 这里处理的是i32，暂时没有考虑别的情况 */
            IrValue left = new IrValue(IrIntegerType.get32(), "-1");
            IrBinaryInst mulExp = new IrBinaryInst(IrIntegerType.get32(),
                    IrInstructionType.Mul, left, genIrInstructionFromUnaryExp(unaryExp));
            int cnt = this.functionCnt.getCnt();
            String name = "%_LocalVariable" + cnt;
            mulExp.setName(name);
            ret = mulExp;
            this.instructions.add(mulExp);
        } else if (op.getType().equals(TokenType.NOT)) {
            /* TODO : 本次作业不涉及条件运算 */
        } else {
            System.out.println("ERROR in IrInstructionBuilder : should not reach here");
        }
        return ret;
    }

    private void addVarSymbol(SymbolVar symbolVar) {
        this.symbolTable.addSymol(symbolVar);
    }

    private void genIrInstructionFromStmtAssign() {
        LVal lval = stmtAssign.getLval();
        Exp exp = stmtAssign.getExp();
        IrValue left = genIrInstructionFromLVal(lval, true);
        IrValue right = genIrInstructionFromExp(exp);
        /* left := right */
        IrStore store = new IrStore(right, left);
        this.instructions.add(store);
    }

    private void genIrInstructionFromStmtBreak() {
        /* TODO : 本次作业不涉及 */
    }

    private void genIrInstructionFromStmtContinue() {
        /* TODO : 本次作业不涉及 */
    }

    private void genIrInstructionFromStmtReturn() {
        IrRet ret;
        if (this.stmtReturn.hasExp()) {
            Exp exp = this.stmtReturn.getExp();
            IrValue retVar = genIrInstructionFromExp(exp);
            ret = new IrRet(retVar);
        } else {
            ret = new IrRet();
        }
        this.instructions.add(ret);
    }

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
        IrStore store = new IrStore(irCall, left);
        this.instructions.add(store);
    }

    private void genIrInstructionFromStmtPrint() {
        FormatString formatString = this.stmtPrint.getFormatString();
        ArrayList<Exp> exps = this.stmtPrint.getExps();
        Token tokenString = formatString.getToken();
        String string = tokenString.getContent();
        char[] chars = string.substring(1, string.length() - 1).toCharArray();
        int len = chars.length;
        int cnt = 0;
        for (int i = 0; i < len; i++) {
            char c = chars[i];
            IrCall irCall;
            if (c == '%') {
                IrValue value = genIrInstructionFromExp(exps.get(cnt));
                // value.setName("i32 " + value.getName());
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
        genIrInstructionFromExp(exp);
    }

}
