package frontend.parser.expression.primaryexp;

import frontend.parser.terminal.IntConst;
import middle.symbol.SymbolTable;

/**
 * Number 非终结符
 * Number -> IntConst
 */
public class Number implements PrimaryExpEle {
    private final String name = "<Number>";
    private IntConst intConst;

    public Number(IntConst intConst) {
        this.intConst = intConst;
    }

    public IntConst getIntConst() {
        return intConst;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(intConst.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.valueOf(this.intConst.getNum());
    }

    @Override
    public int getDimension() {
        return 0;
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        return this.intConst.getNum();
    }
}
