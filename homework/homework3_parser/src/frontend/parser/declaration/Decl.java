package frontend.parser.declaration;

import frontend.parser.SyntaxNode;

public class Decl implements SyntaxNode {
    private final String name = "<Decl>";
    private DeclEle declEle;

    public Decl(DeclEle declEle) {
        this.declEle = declEle;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        /* not output BType according to assignment requirement */
        sb.append(this.declEle.syntaxOutput());
        return sb.toString();
    }
}
