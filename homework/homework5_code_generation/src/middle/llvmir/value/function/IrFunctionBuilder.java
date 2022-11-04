package middle.llvmir.value.function;

import frontend.lexer.TokenType;
import frontend.parser.function.FuncDef;
import frontend.parser.function.FuncFParam;
import frontend.parser.function.FuncFParams;
import frontend.parser.function.MainFuncDef;
import middle.llvmir.IrModule;
import middle.llvmir.IrValue;
import middle.llvmir.type.IrFunctionType;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.type.IrVoidType;
import middle.llvmir.value.basicblock.IrBasicBlockBuilder;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;
import middle.symbol.SymbolVar;

import java.util.ArrayList;

/**
 * LLVM IR Function Builder
 * LLVM IR 函数生成器
 */
public class IrFunctionBuilder {
    private SymbolTable symbolTable; // 函数体所处符号表
    private FuncDef funcDef; //
    private IrModule module; // 父Module
    private MainFuncDef mainFuncDef; // 主函数
    private IrFunctionCnt functionCnt; // 函数变量名计数器
    private SymbolFunc symbolFunc;

    public IrFunctionBuilder(SymbolTable symbolTable, FuncDef funcDef) {
        this.symbolTable = symbolTable;
        this.funcDef = funcDef;
        this.functionCnt = new IrFunctionCnt();
    }

    public IrFunctionBuilder(SymbolTable symbolTable, FuncDef funcDef, IrModule module) {
        this(symbolTable, funcDef);
        this.module = module;
        this.mainFuncDef = null;
    }

    public IrFunctionBuilder(SymbolTable symbolTable, MainFuncDef mainFuncDef, IrModule module) {
        this.symbolTable = symbolTable;
        this.mainFuncDef = mainFuncDef;
        this.funcDef = null;
        this.module = module;
        this.functionCnt = new IrFunctionCnt();
    }

    /**
     * ---------- 生成LLVM IRFunction ----------
     * 因为SysY是非分程序结构语言，因此函数内不能嵌套定义函数，因此返回的是单个对象
     */

    /* 进行普通函数和主函数的区别的分发 */
    public IrFunction genIrFunction() {
        if (this.mainFuncDef == null) {
            addFuncSymbol(this.funcDef);
            return genIrFunctionNormal();
        } else {
            addMainFuncSymbol(this.mainFuncDef);
            return genIrFunctionMain();
        }
    }

    /* 普通函数 */
    private IrFunction genIrFunctionNormal() {
        /* 为IrFunctionType构建retType */
        IrValueType retType; // IrFunctionType的属性
        if (this.funcDef.getRetType().equals("int")) {
            /* 返回值为int */
            retType = IrIntegerType.get32();
        } else {
            /* 返回值为void */
            retType = IrVoidType.getVoidType();
        }
        /* TODO : 将形参填表 */
        /* 为IrFunctionType构建List<IrValueType>paramTypes */
        ArrayList<IrValueType> paramTypes = new ArrayList<>(); // IrFunctionType的属性
        FuncFParams funcFParams = this.funcDef.getFuncFParams();
        if (funcFParams != null) {
            FuncFParam first = funcFParams.getFirst();
            if (first != null) {
                // 第一个参数（如果有）
                paramTypes.add(genParamType(first));
                // TODO : 使用函数变量名计数器计算变量名并填表
                addSymbol(first);
                ArrayList<FuncFParam> funcFParamArrayList = funcFParams.getFuncFParams();
                if (funcFParamArrayList != null && funcFParamArrayList.size() != 0) {
                    // 后续参数（如果有）
                    for (FuncFParam index : funcFParamArrayList) {
                        paramTypes.add(genParamType(index));
                        // TODO : 使用函数变量名计数器计算变量名并填表
                        addSymbol(index);
                    }
                }
            }
        }
        IrFunctionType irFunctionType = new IrFunctionType(retType, paramTypes);
        // IrFunction irFunction = new IrFunction(irFunctionType, this.module);
        // IrFunction irFunction = new IrFunction(irFunctionType, this.module,
        // "@" + this.funcDef.getName());

        IrFunction irFunction = new IrFunction(irFunctionType, this.module,
                "@" + this.funcDef.getName(), functionCnt);
        /* 将函数名加入符号表 */
        this.symbolFunc.setValue(irFunction);
        /* 解析Block */
        IrBasicBlockBuilder basicBlockBuilder = new IrBasicBlockBuilder(this.symbolTable,
                funcDef.getBlock(), this.functionCnt);
        irFunction.addAllIrBasicBlock(basicBlockBuilder.genIrBasicBlock());
        return irFunction;
    }

    /* 主函数，返回值一定为int，没有参数 */
    private IrFunction genIrFunctionMain() {
        /* 返回值一定为int32 */
        IrValueType retType = IrIntegerType.get32();
        /* 参数列表为空 */
        ArrayList<IrValueType> paramTypes = new ArrayList<>();
        IrFunctionType irFunctionType = new IrFunctionType(retType, paramTypes);
        IrFunction irFunction = new IrFunction(irFunctionType, this.module);
        /* 将函数名加入符号表 */
        this.symbolFunc.setValue(irFunction);
        irFunction.setName("@main");
        /* 解析Block */
        IrBasicBlockBuilder basicBlockBuilder = new IrBasicBlockBuilder(this.symbolTable,
                mainFuncDef.getBlock(), this.functionCnt);
        irFunction.addAllIrBasicBlock(basicBlockBuilder.genIrBasicBlock());
        return irFunction;
    }

    /* 分析FuncFParam并获得形参的ValueType */
    private IrValueType genParamType(FuncFParam funcFParam) {
        int dimension = funcFParam.getDimension();
        IrValueType type = null;
        if (dimension == 0) {
            type = IrIntegerType.get32();
        } else if (dimension == 1) {
            /* TODO : 本次作业不涉及数组 */
        } else if (dimension == 2) {
            /* TODO : 本次作业不涉及数组 */
        } else {
            System.out.println("ERROR in IrFunctionBuilder : should not reach here");
        }
        return type;
    }

    // TODO : 使用函数变量名计数器计算变量名并填表
    private void addSymbol(FuncFParam funcFParam) {
        // 用于生成该变量在LLVM IR中的名字
        int cnt = this.functionCnt.getCnt();
        String name = "%_LocalVariable" + cnt;
        // 获取当前参数的维度
        int dimension = funcFParam.getDimension();
        if (dimension == 0) {
            IrValue value = new IrValue(IrIntegerType.get32(), name, true);
            SymbolVar symbolVar = new SymbolVar(funcFParam.getName(),
                    SymbolType.VAR, dimension, value);
            this.symbolTable.addSymol(symbolVar);
            // 也要将形参符号加入函数符号中，以便函数调用
            this.symbolFunc.addSymbol(symbolVar);
        } else if (dimension == 1) {
            /* TODO : 本次作业不涉及数组 */
        } else if (dimension == 2) {
            /* TODO : 本次作业不涉及数组 */
        } else {
            System.out.println("ERROR in IrFunctionBuilder.addSymbol : should not reach here");
        }
    }

    private void addFuncSymbol(FuncDef funcDef) {
        SymbolType symbolType = SymbolType.FUNC;
        SymbolFunc symbolFunc = new SymbolFunc(0, funcDef.getName(), symbolType);
        if (funcDef.getFuncType().getType().equals(TokenType.VOIDTK)) {
            symbolFunc.setDimension(-1);
        } else if (funcDef.getFuncType().getType().equals(TokenType.INTTK)) {
            symbolFunc.setDimension(0);
        } else {
            System.out.println("ERROR in IrBuilder.genIrModule : should not reach here");
        }
        symbolTable.getParent().addSymol(symbolFunc);
        this.symbolFunc = symbolFunc;
    }

    private void addMainFuncSymbol(MainFuncDef mainFuncDef) {
        SymbolType symbolType = SymbolType.FUNC;
        SymbolFunc symbolFunc = new SymbolFunc(0, mainFuncDef.getName(), symbolType);
        symbolFunc.setDimension(0);
        symbolTable.getParent().addSymol(symbolFunc);
        this.symbolFunc = symbolFunc;
    }
}
