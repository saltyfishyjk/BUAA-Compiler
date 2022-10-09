package middle.symbol;

import java.util.ArrayList;

public class SymbolFunc extends Symbol {
    private ArrayList<Symbol> symbols = new ArrayList<>();

    public SymbolFunc(int lineNum, String name, SymbolType symbolType) {
        super(lineNum, name, symbolType);
    }

    public SymbolFunc(int lineNum, String name, SymbolType symbolType, int dimension) {
        super(lineNum, name, symbolType);
        this.setDimension(dimension);
    }

    public void addSymbol(Symbol symbol) {
        this.symbols.add(symbol);
    }

    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }
}
