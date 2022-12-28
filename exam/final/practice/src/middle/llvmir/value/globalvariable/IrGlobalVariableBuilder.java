package middle.llvmir.value.globalvariable;

import frontend.parser.declaration.Decl;
import frontend.parser.declaration.DeclEle;
import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.declaration.constant.ConstDef;
import frontend.parser.declaration.constant.constinitval.ConstInitVal;
import frontend.parser.declaration.constant.constinitval.ConstInitValMulti;
import frontend.parser.declaration.variable.VarDecl;
import frontend.parser.declaration.variable.initval.InitVal;
import frontend.parser.declaration.variable.initval.InitVals;
import frontend.parser.declaration.variable.vardef.VarDef;
import frontend.parser.declaration.variable.vardef.VarDefEle;
import frontend.parser.declaration.variable.vardef.VarDefInit;
import frontend.parser.declaration.variable.vardef.VarDefNull;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.Exp;
import frontend.parser.terminal.Ident;
import middle.llvmir.IrValue;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.value.constant.IrConstantArray;
import middle.llvmir.value.constant.IrConstantInt;
import middle.symbol.Symbol;
import middle.symbol.SymbolCon;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;
import middle.symbol.SymbolVar;

import java.util.ArrayList;

/**
 * LLVM IR Global Variable Builder
 * LLVM IR 全局变量生成器
 * 从这里开始，对于
 */
public class IrGlobalVariableBuilder {
    private SymbolTable symbolTable;
    private Decl decl;

    public IrGlobalVariableBuilder(SymbolTable symbolTable, Decl decl) {
        this.symbolTable = symbolTable;
        this.decl = decl;
    }

    /**
     * ---------- 生成LLVM IR GlobalVariable ----------
     * 因为一条声明语句可能含有多个声明，因此返回的是List
     */
    public ArrayList<IrGlobalVariable> genIrGlobalVariable() {
        ArrayList<IrGlobalVariable> variables = new ArrayList<>();
        DeclEle ele = this.decl.getDeclEle();
        if (ele instanceof ConstDecl) {
            ConstDecl constDecl = (ConstDecl) ele;
            variables.add(genIrConstGlobalVariable(constDecl.getFirst()));
            for (ConstDef def : constDecl.getConstDefs()) {
                variables.add(genIrConstGlobalVariable(def));
            }
        } else if (ele instanceof VarDecl) {
            VarDecl varDecl = (VarDecl) ele;
            variables.add(genIrVarGlobalVariable(varDecl.getFirst()));
            for (VarDef def : varDecl.getVarDefs()) {
                variables.add(genIrVarGlobalVariable(def));
            }
        } else {
            System.out.println("ERROR in IrBuilder.genIrGlobalVariable! Need Const or Var Decl");
        }
        return variables;
    }

    /* 填写符号表，并通过一条ConstDef生成一条IrGlobalVariable */
    private IrGlobalVariable genIrConstGlobalVariable(ConstDef def) {
        Ident ident = def.getIdent();
        SymbolType symbolType;
        int dimension;
        if (def.getLeftBracks().size() == 0) {
            symbolType = SymbolType.CON;
            dimension = 0;
        } else if (def.getLeftBracks().size() == 1) {
            symbolType = SymbolType.CON1;
            dimension = 1;
        } else if (def.getLeftBracks().size() == 2) {
            symbolType = SymbolType.CON2;
            dimension = 2;
        } else {
            symbolType = null;
            dimension = -1;
            System.out.println("ERROR in IrGlobalVariableBuilder! should not reach here");
        }
        /* 生成常量符号 */
        Symbol symbol = new SymbolCon(ident.getLineNum(), ident.getName(),
                symbolType, dimension);
        setInitVal(symbol, def.getConstInitval());
        symbolTable.addSymol(symbol);
        IrGlobalVariable globalVariable = null;
        boolean isConst = true;
        IrValueType type = IrIntegerType.get32();
        if (dimension == 0) {
            IrConstantInt constantInt = new IrConstantInt(type,
                    ((SymbolCon) symbol).getInitVal());
            // @是全局变量的标记
            String name = "@_GlobalConst" + IrGlobalVariableCnt.getCnt();

            globalVariable = new IrGlobalVariable(name, type, isConst, constantInt);
            symbol.setValue(globalVariable);
        } else if (dimension == 1) {
            /* 1维数组 */
            ArrayList<Integer> initval1 = ((SymbolCon)symbol).getInitval1();
            ArrayList<IrConstantInt> constantInts = new ArrayList<>();
            int len = initval1.size();
            for (int i = 0; i < len; i++) {
                IrConstantInt irConstantInt = new IrConstantInt(type,
                        initval1.get(i));
                constantInts.add(irConstantInt);
            }
            int cnt = IrGlobalVariableCnt.getCnt();
            String name = "@_GlobalConst" + cnt;
            IrConstantArray constantArray = new IrConstantArray(type, 1, len, constantInts);
            globalVariable = new IrGlobalVariable(name, type, isConst, constantArray);
            symbol.setValue(globalVariable);
        } else if (dimension == 2) {
            /* 2维数组 */
            ArrayList<ArrayList<Integer>> initval2 = ((SymbolCon)symbol).getInitval2();
            ArrayList<ArrayList<IrConstantInt>> constantInts = new ArrayList<>();
            int dimension1 = initval2.size();
            int dimension2 = initval2.get(0).size();
            for (int i = 0; i < dimension1; i++) {
                ArrayList<IrConstantInt> temp = new ArrayList<>();
                for (int j = 0; j < dimension2; j++) {
                    IrConstantInt irConstantInt = new IrConstantInt(type, initval2.get(i).get(j));
                    temp.add(irConstantInt);
                }
                constantInts.add(temp);
            }
            IrConstantArray constantArray = new IrConstantArray(type,
                    2, dimension1, dimension2, constantInts);
            int cnt = IrGlobalVariableCnt.getCnt();
            String name = "@_GlobalConst" + cnt;
            globalVariable = new IrGlobalVariable(name, type, isConst, constantArray);
            symbol.setValue(globalVariable);
        } else {
            System.out.println("ERROR in IrGlobalVariableBuilder! should not reach here");
        }
        return globalVariable;
    }

    /* 通过一条VarDef生成一条IrGlobalVariable */
    private IrGlobalVariable genIrVarGlobalVariable(VarDef def) {
        Ident ident = def.getIdent();
        SymbolType symbolType;
        int dimension;
        if (def.getVarDefEle().getLeftBraces().size() == 0) {
            symbolType = SymbolType.CON;
            dimension = 0;
        } else if (def.getVarDefEle().getLeftBraces().size() == 1) {
            symbolType = SymbolType.CON1;
            dimension = 1;
        } else if (def.getVarDefEle().getLeftBraces().size() == 2) {
            symbolType = SymbolType.CON2;
            dimension = 2;
        } else {
            symbolType = null;
            dimension = -1;
            System.out.println("ERROR in IrGlobalVariableBuilder! should not reach here");
        }
        Symbol symbol = new SymbolVar(ident.getLineNum(), ident.getName(),
                symbolType, dimension);
        setInitVal(symbol, def.getVarDefEle());
        symbolTable.addSymol(symbol);
        IrGlobalVariable globalVariable = null;
        IrValueType type = IrIntegerType.get32(); // 32整数
        String name = "@_GlobalVariable" + IrGlobalVariableCnt.getCnt(); // 自增变量名
        boolean isConst = false;
        if (dimension == 0) {
            IrConstantInt constantInt = new IrConstantInt(IrIntegerType.get32(),
                    ((SymbolVar) symbol).getInitVal());
            // @是全局变量的标记
            globalVariable = new IrGlobalVariable(name, type, isConst, constantInt);
            /* 设置父类对象的dimension */
            ((IrValue)globalVariable).setDimension(globalVariable.getDimension());
            symbol.setValue(globalVariable);
        } else if (dimension == 1) {
            ArrayList<Integer> initval1 = ((SymbolVar)symbol).getInitVal1();
            ArrayList<IrConstantInt> constantInts = new ArrayList<>();
            int len = initval1.size();
            boolean all0 = ((SymbolVar) symbol).getAll0();
            if (all0) {
                /* 什么都不做 */
            } else {
                for (int i = 0; i < len; i++) {
                    IrConstantInt irConstantInt = new IrConstantInt(type, initval1.get(i));
                    constantInts.add(irConstantInt);
                }
            }
            
            IrConstantArray constantArray = new IrConstantArray(type, 1, len, constantInts);
            globalVariable = new IrGlobalVariable(name, type, isConst, constantArray);
            /* 设置父类对象的dimension */
            ((IrValue)globalVariable).setDimension(globalVariable.getDimension());
            ((IrValue)globalVariable).setDimension1(globalVariable.getDimension1());
            symbol.setValue(globalVariable);
        } else if (dimension == 2) {
            ArrayList<ArrayList<Integer>> initval2 = ((SymbolVar) symbol).getInitVal2();
            ArrayList<ArrayList<IrConstantInt>> constantInts = new ArrayList<>();
            int dimension1 = initval2.size();
            int dimension2 = initval2.get(0).size();
            boolean all0 = ((SymbolVar) symbol).getAll0();
            if (all0) {
                /* 什么都不做 */
            } else {
                for (int i = 0; i < dimension1; i++) {
                    ArrayList<IrConstantInt> temp = new ArrayList<>();
                    for (int j = 0; j < dimension2; j++) {
                        IrConstantInt irConstantInt = new IrConstantInt(type, 
                                initval2.get(i).get(j));
                        temp.add(irConstantInt);
                    }
                    constantInts.add(temp);
                }
            }
            IrConstantArray constantArray = new IrConstantArray(type,
                    2, dimension1, dimension2, constantInts);
            globalVariable = new IrGlobalVariable(name, type, isConst, constantArray);
            /* 设置父类对象的dimension */
            ((IrValue)globalVariable).setDimension(globalVariable.getDimension());
            ((IrValue)globalVariable).setDimension1(globalVariable.getDimension1());
            ((IrValue)globalVariable).setDimension2(globalVariable.getDimension2());
            symbol.setValue(globalVariable);
        } else {
            System.out.println("ERROR in IrGlobalVariableBuilder! should not reach here");
        }
        return globalVariable;
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

    /**
     * 添加变量符号初值
     */
    private void setInitVal(Symbol symbol, VarDefEle ele) {
        int dimension = symbol.getDimension();
        SymbolVar var = (SymbolVar) symbol;
        if (ele instanceof VarDefInit) {
            // 有初始化的全局变量
            VarDefInit defInit = (VarDefInit) ele;
            InitVal initVal = defInit.getInitVal();
            if (dimension == 0) {
                var.setInitVal(((Exp)initVal.getInitValEle()).calcNode(this.symbolTable));
            } else if (dimension == 1) {
                // 1维数组
                InitVals temp = (InitVals) initVal.getInitValEle();
                ArrayList<InitVal> initVals = temp.getAllInitVals();
                ArrayList<Integer> inits = new ArrayList<>();
                int dimension1 = initVals.size();
                for (int i = 0; i < dimension1; i++) {
                    inits.add(((Exp)initVals.get(i).getInitValEle()).calcNode(this.symbolTable));
                }
                var.setInitVal1(inits);
            } else if (dimension == 2) {
                // 2维数组
                InitVals temp = (InitVals) initVal.getInitValEle();
                ArrayList<InitVal> initVals = temp.getAllInitVals();
                // initVals的元素是InitVals
                ArrayList<ArrayList<Integer>> inits = new ArrayList<>();
                int dimension1 = initVals.size();
                int dimension2 = ((InitVals)initVals.get(0).
                        getInitValEle()).getAllInitVals().size();
                for (int i = 0; i < dimension1; i++) {
                    InitVals indexI = (InitVals)initVals.get(i).getInitValEle();
                    ArrayList<InitVal> indexJ = indexI.getAllInitVals();
                    ArrayList<Integer> initInside = new ArrayList<>();
                    for (int j = 0; j < dimension2; j++) {
                        InitVal now = indexJ.get(j);
                        initInside.add(((Exp)now.getInitValEle()).calcNode(this.symbolTable));
                    }
                    inits.add(initInside);
                }
                var.setInitVal2(inits);
            } else {
                System.out.println("ERROR in IrGlobalVariableBuilder : should not reach here");
            }
        } else {
            // 无初始化的全局变量
            VarDefNull defNull = (VarDefNull)ele;
            if (dimension == 0) {
                var.setInitVal(0); // 为初始化的全局变量的初值为0
            } else if (dimension == 1) {
                // 1维数组
                int dimension1 = defNull.getConstExps().get(0).calcNode(this.symbolTable);
                ArrayList<Integer> inits = new ArrayList<>();
                for (int i = 0; i < dimension1; i++) {
                    inits.add(0);
                }
                var.setInitVal1(inits);
                var.setAll0(true);
            } else if (dimension == 2) {
                // 2维数组
                int dimension1 = defNull.getConstExps().get(0).calcNode(this.symbolTable);
                int dimension2 = defNull.getConstExps().get(1).calcNode(this.symbolTable);
                ArrayList<ArrayList<Integer>> inits = new ArrayList<>();
                for (int i = 0; i < dimension1; i++) {
                    ArrayList<Integer> temp = new ArrayList<>();
                    for (int j = 0; j < dimension2; j++) {
                        temp.add(0);
                    }
                    inits.add(temp);
                }
                var.setInitVal2(inits);
                var.setAll0(true);
            } else {
                System.out.println("ERROR in IrGlobalVariableBuilder : should not reach here");
            }
        }
    }
}
