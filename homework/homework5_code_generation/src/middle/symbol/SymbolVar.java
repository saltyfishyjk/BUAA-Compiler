package middle.symbol;

public class SymbolVar extends Symbol {
    public SymbolVar(int lineNum, String name, SymbolType symbolType) {
        super(lineNum, name, symbolType);
    }

    public SymbolVar(int lineNum, String name, SymbolType symbolType, int dimension) {
        this(lineNum, name, symbolType);
        this.setDimension(dimension);
    }
}
