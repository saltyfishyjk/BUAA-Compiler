package frontend.parser.expression;

import frontend.parser.SyntaxNode;
import frontend.parser.expression.multiexp.AddExp;

public class ConstExp implements SyntaxNode {
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
}
