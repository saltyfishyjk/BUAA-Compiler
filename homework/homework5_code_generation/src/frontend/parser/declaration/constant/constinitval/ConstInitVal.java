package frontend.parser.declaration.constant.constinitval;

import frontend.parser.SyntaxNode;

public class ConstInitVal implements SyntaxNode {
    private final String name = "<ConstInitVal>";
    private ConstInitValEle constInitValEle;

    public ConstInitVal(ConstInitValEle constInitValEle) {
        this.constInitValEle = constInitValEle;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(constInitValEle.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
