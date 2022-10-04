package frontend.parser.expression.unaryexp;

import frontend.parser.SyntaxNode;
import frontend.parser.expression.unaryexp.UnaryExpEle;

public class UnaryExp implements SyntaxNode {
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
}
