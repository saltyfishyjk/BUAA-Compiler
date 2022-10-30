package frontend.parser.expression;

import frontend.parser.declaration.constant.constinitval.ConstInitValEle;
import frontend.parser.expression.multiexp.AddExp;
import middle.symbol.SymbolTable;
import middle.symbol.ValNode;

public class ConstExp implements ConstInitValEle, ValNode {
    private final String name = "<ConstExp>";
    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        return this.addExp.calcNode(symbolTable);
    }
}
