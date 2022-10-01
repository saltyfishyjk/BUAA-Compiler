package frontend.parser.expression;

import frontend.parser.expression.multiexp.AddExp;

/**
 * 表达式
 */
public class Exp {
    private final String name = "<Exp>";
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }
}
