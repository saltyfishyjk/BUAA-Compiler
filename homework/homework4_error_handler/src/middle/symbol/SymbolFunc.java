package middle.symbol;

public class SymbolFunc extends Symbol {
    public SymbolFunc(int lineNum, String name, SymbolType symbolType) {
        super(lineNum, name, symbolType);
    }

    public SymbolFunc(int lineNum, String name, SymbolType symbolType, int dimension) {
        super(lineNum, name, symbolType);
        this.setDimension(dimension);
    }
}
