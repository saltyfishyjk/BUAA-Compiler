package middle.symbol;

import middle.llvmir.IrValue;

/**
 * 符号
 */
public class Symbol {
    private int lineNum; // 从1开始
    private String name; // SysY中的名字
    private SymbolType symbolType = null;
    private int dimension; // 维数
    private IrValue value; // LLVM IR中的Value，在遍历AST的时候需要维护这一映射

    public Symbol(int lineNum, String name, SymbolType symbolType) {
        this.lineNum = lineNum;
        this.name = name;
        this.symbolType = symbolType;
    }

    public Symbol(String name, SymbolType symbolType, IrValue value) {
        this(0, name, symbolType); // lineNum不重要，在中间代码阶段没有意义
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getLineNum() {
        return lineNum;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    public IrValue getValue() {
        return value;
    }

    public void setValue(IrValue value) {
        this.value = value;
    }
}