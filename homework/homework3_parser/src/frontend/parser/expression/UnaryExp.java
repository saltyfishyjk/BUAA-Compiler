package frontend.parser.expression;

import frontend.parser.SyntaxNode;

public class UnaryExp implements SyntaxNode {
    private final String name = "UnaryExp";
    private UnaryExpEle unaryExpEle;

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        /* TODO */
        return sb.toString();
    }
}
