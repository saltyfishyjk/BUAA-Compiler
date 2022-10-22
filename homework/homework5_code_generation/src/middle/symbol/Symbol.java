package middle.symbol;

/**
 * 符号
 */
public class Symbol {
    private int lineNum; // 从1开始
    private String name;
    private SymbolType symbolType = null;
    private int dimension; // 维数

    public Symbol(int lineNum, String name, SymbolType symbolType) {
        this.lineNum = lineNum;
        this.name = name;
        this.symbolType = symbolType;
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
}