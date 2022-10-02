package frontend.parser.expression;

import frontend.parser.declaration.constant.constinitval.ConstInitValEle;
import frontend.parser.expression.multiexp.AddExp;

public class ConstExp implements ConstInitValEle {
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
