package middle.symbol;

import java.util.HashMap;

public class SymbolFunc extends Symbol {
    /* 参数位置 -> 参数符号对象 */
    private HashMap<Integer, Symbol> params = new HashMap<>();

    public SymbolFunc(int lineNum, String name, SymbolType symbolType) {
        super(lineNum, name, symbolType);
    }

    public SymbolFunc(int lineNum, String name, SymbolType symbolType, int dimension) {
        super(lineNum, name, symbolType);
        this.setDimension(dimension);
    }
}
