package frontend.parser.expression;

import frontend.parser.expression.multiexp.AddExp;

public class ConstExp {
    private AddExp addExp;
    private final String name = "<ConstExp>";

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }
}
