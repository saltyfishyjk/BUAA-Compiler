package frontend.parser.expression;

import frontend.parser.declaration.variable.initval.InitValEle;
import frontend.parser.expression.multiexp.AddExp;

/**
 * 表达式
 */
public class Exp implements InitValEle {
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
}
