package middle.symbol;

import java.util.HashMap;

/**
 * 符号表
 */
public class SymbolTable {
    /* 符号名 -> 符号obj */
    private HashMap<String, Symbol> symbols;
    private SymbolTable parent = null;
    private int cycleDepth;

    public SymbolTable() {
        this.symbols = new HashMap<>();
        this.parent = null;
        this.cycleDepth = 0;
    }

    public SymbolTable(SymbolTable parent) {
        this.symbols = new HashMap<>();
        this.parent = parent;
        this.cycleDepth = parent.getCycleDepth();
    }

    public int getCycleDepth() {
        return cycleDepth;
    }

    public void setCycleDepth(int cycleDepth) {
        this.cycleDepth = cycleDepth;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public void addSymol(Symbol symbol) {
        this.symbols.put(symbol.getName(), symbol);
    }

    /* 检测出b类错误返回true，否则返回false */
    public boolean checkBTypeError(Symbol symbol) {
        for (String name : this.symbols.keySet()) {
            if (name.equals(symbol.getName())) {
                return true;
            }
        }
        return false;
    }

    /* 检测出c类错误返回true，否则返回false */
    public boolean checkCTypeError(String name) {
        for (String index : this.symbols.keySet()) {
            if (index.equals(name)) {
                return false;
            }
        }
        if (this.hasParent()) {
            return this.getParent().checkCTypeError(name);
        } else {
            return true;
        }
    }

    /* 获取指定名字对应的参数对象，如果没有则返回null */
    public Symbol getSymbol(String name) {
        if (this.symbols.containsKey(name)) {
            return this.symbols.get(name);
        }
        if (this.hasParent()) {
            return this.parent.getSymbol(name);
        }
        return null;
    }
}
