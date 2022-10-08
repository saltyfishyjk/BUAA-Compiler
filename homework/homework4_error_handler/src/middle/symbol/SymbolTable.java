package middle.symbol;

import java.util.HashMap;

/**
 * 符号表
 */
public class SymbolTable {
    private HashMap<String, Symbol> symbols = new HashMap<>();
    private SymbolTable parent = null;

    public SymbolTable getParent() {
        return parent;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public void addSymol(Symbol symbol) {
        this.symbols.put(symbol.getName(), symbol);
    }
}
