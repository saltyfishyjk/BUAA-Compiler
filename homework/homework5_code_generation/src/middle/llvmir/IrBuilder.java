package middle.llvmir;

import frontend.parser.CompUnit;
import frontend.parser.declaration.Decl;
import frontend.parser.function.FuncDef;
import middle.llvmir.value.function.IrFunction;
import middle.llvmir.value.function.IrFunctionBuilder;
import middle.llvmir.value.globalvariable.IrGlobalVariable;
import middle.llvmir.value.globalvariable.IrGlobalVariableBuilder;
import middle.symbol.SymbolTable;
import java.util.ArrayList;

/**
 * IrBuilder
 * 将抽象语法树翻译成LLVM IR
 * 为了精简我们的代码结构，向低耦合高内聚更好地进发，
 * 此次我们将LLVM IR四种层次的解析器置于本类中一起实现
 */
public class IrBuilder {
    private CompUnit compUnit; // 作为构造器参数，为语法树的顶层节点，其蕴含了语法树的全部信息
    private IrModule module; // 顶层Module单元
    private SymbolTable symbolTable; // 当前指向的符号表

    public IrBuilder(CompUnit compUnit) {
        this.compUnit = compUnit;
        this.module = new IrModule(); // 生成新的IrModule
        this.symbolTable = new SymbolTable();
    }

    /**
     * ---------- 生成 LLVM IR Module ----------
     */

    public IrModule genIrModule() {
        /* 生成全局变量 */
        for (Decl decl : this.compUnit.getDecls()) {
            IrGlobalVariableBuilder variableBuilder =
                    new IrGlobalVariableBuilder(this.symbolTable, decl);
            ArrayList<IrGlobalVariable> temp = variableBuilder.genIrGlobalVariable();
            for (IrGlobalVariable index : temp) {
                if (index == null) {
                    continue;
                }
                this.module.addIrGlobalVariables(index);
            }
        }
        for (FuncDef funcDef : this.compUnit.getFuncDefs()) {
            SymbolTable table = new SymbolTable(symbolTable); // 进入新的函数，进入新的子表
            IrFunctionBuilder functionBuilder = new IrFunctionBuilder(table, funcDef, this.module);
            IrFunction irFunction = functionBuilder.genIrFunction();
            this.module.addIrFunction(irFunction);
        }
        SymbolTable table = new SymbolTable(symbolTable);
        IrFunctionBuilder functionBuilder = new IrFunctionBuilder(table,
                this.compUnit.getMainFuncDef(), this.module);
        this.module.addIrFunction(functionBuilder.genIrFunction());
        return this.module;
    }
}
