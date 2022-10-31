package middle.llvmir.value.function;

import frontend.parser.function.FuncDef;
import frontend.parser.function.FuncFParam;
import frontend.parser.function.FuncFParams;
import frontend.parser.function.MainFuncDef;
import middle.llvmir.IrModule;
import middle.llvmir.type.IrFunctionType;
import middle.llvmir.type.IrIntegerType;
import middle.llvmir.type.IrValueType;
import middle.llvmir.type.IrVoidType;
import middle.llvmir.value.basicblock.IrBasicBlockBuilder;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * LLVM IR Function Builder
 * LLVM IR 函数生成器
 */
public class IrFunctionBuilder {
    private SymbolTable symbolTable; // 当前所处符号表
    private FuncDef funcDef; //
    private IrModule module; // 父Module
    private MainFuncDef mainFuncDef; // 主函数

    public IrFunctionBuilder(SymbolTable symbolTable, FuncDef funcDef) {
        this.symbolTable = symbolTable;
        this.funcDef = funcDef;
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
    }

    /**
     * ---------- 生成LLVM IRFunction ----------
     * 因为SysY是非分程序结构语言，因此函数内不能嵌套定义函数，因此返回的是单个对象
     */

    /* 进行普通函数和主函数的区别的分发 */
    public IrFunction genIrFunction() {
        if (this.mainFuncDef == null) {
            return genIrFunctionNormal();
        } else {
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
        /* 为IrFunctionType构建List<IrValueType>paramTypes */
        ArrayList<IrValueType> paramTypes = new ArrayList<>(); // IrFunctionType的属性
        FuncFParams funcFParams = this.funcDef.getFuncFParams();
        if (funcFParams != null) {
            FuncFParam first = funcFParams.getFirst();
            if (first != null) {
                paramTypes.add(genParamType(first));
                ArrayList<FuncFParam> funcFParamArrayList = funcFParams.getFuncFParams();
                if (funcFParamArrayList != null && funcFParamArrayList.size() != 0) {
                    for (FuncFParam index : funcFParamArrayList) {
                        paramTypes.add(genParamType(index));
                    }
                }
            }
        }
        IrFunctionType irFunctionType = new IrFunctionType(retType, paramTypes);
        IrFunction irFunction = new IrFunction(irFunctionType, this.module);
        /* 解析Block */
        IrBasicBlockBuilder basicBlockBuilder = new IrBasicBlockBuilder(this.symbolTable,
                funcDef.getBlock());
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
        /* 解析Block */
        IrBasicBlockBuilder basicBlockBuilder = new IrBasicBlockBuilder(this.symbolTable,
                mainFuncDef.getBlock());
        irFunction.addAllIrBasicBlock(basicBlockBuilder.genIrBasicBlock());
        return irFunction;
    }

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

}
