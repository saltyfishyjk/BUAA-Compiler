package frontend.parser.expression.unaryexp;

public class UnaryExpOp implements UnaryExpEle {
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    public UnaryExpOp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.unaryOp.syntaxOutput());
        sb.append(this.unaryExp.syntaxOutput());
        return sb.toString();
    }

    @Override
    public int getDimension() {
        return this.unaryExp.getDimension();
    }
}
