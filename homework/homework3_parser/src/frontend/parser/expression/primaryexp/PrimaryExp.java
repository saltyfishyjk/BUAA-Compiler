package frontend.parser.expression.primaryexp;

import frontend.parser.expression.primaryexp.PrimaryExpEle;
import frontend.parser.expression.unaryexp.UnaryExpEle;

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
}
