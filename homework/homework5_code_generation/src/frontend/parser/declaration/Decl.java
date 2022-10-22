package frontend.parser.declaration;

import frontend.parser.statement.blockitem.BlockItemEle;

public class Decl implements BlockItemEle {
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

    @Override
    public int checkReturn() {
        return 0;
    }
}
