package frontend.parser.expression.unaryexp;

import frontend.parser.SyntaxNode;
import frontend.parser.expression.unaryexp.UnaryExpEle;
import middle.symbol.SymbolTable;
import middle.symbol.ValNode;

public class UnaryExp implements SyntaxNode, ValNode {
    private final String name = "<UnaryExp>";
    private UnaryExpEle unaryExpEle;

    public UnaryExp(UnaryExpEle unaryExpEle) {
        this.unaryExpEle = unaryExpEle;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.unaryExpEle.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    public int getDimension() {
        return this.unaryExpEle.getDimension();
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        return this.unaryExpEle.calcNode(symbolTable);
    }

    public UnaryExpEle getUnaryExpEle() {
        return unaryExpEle;
    }
}
