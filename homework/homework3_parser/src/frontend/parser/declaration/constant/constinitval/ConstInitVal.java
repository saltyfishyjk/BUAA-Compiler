package frontend.parser.declaration.constant.constinitval;

import frontend.parser.SyntaxNode;

public class ConstInitVal implements SyntaxNode {
    private final String name = "<constinitval>";
    private ConstInitValEle constInitValEle;

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(constInitValEle.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
