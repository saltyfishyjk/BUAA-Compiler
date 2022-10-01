package frontend.parser.expression;

import frontend.parser.expression.multiexp.AddExp;

public class ConstExp {
    private final String name = "<ConstExp>";
    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }
}
