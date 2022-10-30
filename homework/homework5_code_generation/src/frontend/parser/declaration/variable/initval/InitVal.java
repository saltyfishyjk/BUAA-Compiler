package frontend.parser.declaration.variable.initval;

import frontend.parser.SyntaxNode;

public class InitVal implements SyntaxNode {
    private final String name = "<InitVal>";
    private InitValEle initValEle;

    public InitVal(InitValEle initValEle) {
        this.initValEle = initValEle;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.initValEle.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    public InitValEle getInitValEle() {
        return initValEle;
    }
}
