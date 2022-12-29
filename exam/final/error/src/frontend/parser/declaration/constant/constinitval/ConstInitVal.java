package frontend.parser.declaration.constant.constinitval;

import frontend.parser.SyntaxNode;
import middle.symbol.SymbolTable;
import middle.symbol.ValNode;

public class ConstInitVal implements SyntaxNode, ValNode {
    private final String name = "<ConstInitVal>";
    private ConstInitValEle constInitValEle;

    public ConstInitVal(ConstInitValEle constInitValEle) {
        this.constInitValEle = constInitValEle;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(constInitValEle.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    public ConstInitValEle getConstInitValEle() {
        return constInitValEle;
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        return this.constInitValEle.calcNode(symbolTable);
    }
}
