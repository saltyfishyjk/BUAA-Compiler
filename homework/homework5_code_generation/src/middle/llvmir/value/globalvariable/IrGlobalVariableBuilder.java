package middle.llvmir.value.globalvariable;

import frontend.parser.declaration.Decl;
import frontend.parser.declaration.DeclEle;
import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.declaration.constant.ConstDef;
import frontend.parser.declaration.constant.constinitval.ConstInitVal;
import frontend.parser.declaration.constant.constinitval.ConstInitValMulti;
import frontend.parser.declaration.variable.VarDecl;
import frontend.parser.declaration.variable.vardef.VarDef;
import frontend.parser.declaration.variable.vardef.VarDefEle;
import frontend.parser.declaration.variable.vardef.VarDefInit;
import frontend.parser.declaration.variable.vardef.VarDefNull;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.Exp;
import frontend.parser.terminal.Ident;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrValueType;
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
        Symbol symbol = new SymbolCon(ident.getLineNum(), ident.getName(),
                symbolType, dimension);
        setInitVal(symbol, def.getConstInitval());
        symbolTable.addSymol(symbol);
        IrGlobalVariable globalVariable = null;
        if (dimension == 0) {
            IrConstantInt constantInt = new IrConstantInt(IrIntegerType.get32(),
                    ((SymbolCon) symbol).getInitVal());
            // @是全局变量的标记
            String name = "@_GlobalConst" + IrGlobalVariableCnt.getCnt();
            IrValueType type = IrIntegerType.get32();
            boolean isConst = true;
            globalVariable = new IrGlobalVariable(name, type, isConst, constantInt);
            symbol.setValue(globalVariable);
        } else if (dimension == 1) {
            /* TODO : 本次作业不涉及数组 */
            return null;
        } else if (dimension == 2) {
            /* TODO : 本次作业不涉及数组 */
            return null;
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
        if (dimension == 0) {
            IrConstantInt constantInt = new IrConstantInt(IrIntegerType.get32(),
                    ((SymbolVar) symbol).getInitVal());
            // @是全局变量的标记
            String name = "@_GlobalVariable" + IrGlobalVariableCnt.getCnt(); // 自增变量名
            IrValueType type = IrIntegerType.get32(); // 32整数
            boolean isConst = false;
            globalVariable = new IrGlobalVariable(name, type, isConst, constantInt);
            symbol.setValue(globalVariable);
        } else if (dimension == 1) {
            /* TODO : 本次作业不涉及数组 */
            return null;
        } else if (dimension == 2) {
            /* TODO : 本次作业不涉及数组 */
            return null;
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

    /**
     * 添加变量符号初值
     */
    private void setInitVal(Symbol symbol, VarDefEle ele) {
        int dimension = symbol.getDimension();
        SymbolVar var = (SymbolVar) symbol;
        if (ele instanceof VarDefInit) {
            // 有初始化的全局变量
            VarDefInit defInit = (VarDefInit) ele;
            if (dimension == 0) {
                var.setInitVal(((Exp)defInit.getInitVal().getInitValEle()).calcNode(symbolTable));
            } else if (dimension == 1) {
                // 一维数组
                /* TODO : 本次作业不涉及数组 */
            } else if (dimension == 2) {
                // 二维数组
                /* TODO : 本次作业不涉及数组 */
            } else {
                System.out.println("ERROR in IrGlobalVariableBuilder : should not reach here");
            }
        } else {
            // 无初始化的全局变量
            VarDefNull defNull = (VarDefNull)ele;
            if (dimension == 0) {
                var.setInitVal(0); // 为初始化的全局变量的初值为0
            } else if (dimension == 1) {
                // 一维数组
                /* TODO : 本次作业不涉及数组 */
            } else if (dimension == 2) {
                // 二维数组
                /* TODO : 本次作业不涉及数组 */
            } else {
                System.out.println("ERROR in IrGlobalVariableBuilder : should not reach here");
            }
        }
    }
}
