package frontend.parser.expression.primaryexp;

import frontend.parser.expression.primaryexp.PrimaryExpEle;
import frontend.parser.expression.unaryexp.UnaryExpEle;
import middle.symbol.SymbolTable;

public class PrimaryExp implements UnaryExpEle {
    private final String name = "<PrimaryExp>";
    private PrimaryExpEle primaryExpEle;

    public PrimaryExp(PrimaryExpEle primaryExpEle) {
        this.primaryExpEle = primaryExpEle;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.primaryExpEle.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    @Override
    public int getDimension() {
        return this.primaryExpEle.getDimension();
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        return this.primaryExpEle.calcNode(symbolTable);
    }

    public PrimaryExpEle getPrimaryExpEle() {
        return primaryExpEle;
    }
}
