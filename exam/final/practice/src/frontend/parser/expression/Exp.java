package frontend.parser.expression;

import frontend.parser.declaration.variable.initval.InitValEle;
import frontend.parser.expression.multiexp.AddExp;
import middle.symbol.SymbolTable;
import middle.symbol.ValNode;

/**
 * 表达式
 */
public class Exp implements InitValEle, ValNode {
    private final String name = "<Exp>";
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.addExp.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    public int getDimension() {
        return addExp.getDimension();
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        return addExp.calcNode(symbolTable);
    }
}
