package frontend.parser.declaration.constant;

import frontend.lexer.Token;
import frontend.parser.declaration.BType;
import frontend.parser.declaration.DeclEle;

import java.util.ArrayList;

public class ConstDecl implements DeclEle {
    private final String name = "<ConstDecl>";
    private Token constTk; // 'const'
    private BType btype;
    private ConstDef first;
    private ArrayList<Token> commas; // commas
    private ArrayList<ConstDef> constDefs; // constDefs
    private Token semicn; // ';'

    public ConstDecl(Token constTk,
                     BType btype,
                     ConstDef first,
                     Token semicn) {
        this.constTk = constTk;
        this.btype = btype;
        this.first = first;
        this.semicn = semicn;
    }

    public ConstDecl(Token constTk,
                     BType btype,
                     ConstDef first,
                     ArrayList<Token> commas,
                     ArrayList<ConstDef> constDefs,
                     Token semicn) {
        this(constTk, btype, first, semicn);
        this.commas = commas;
        this.constDefs = constDefs;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(constTk.syntaxOutput());
        sb.append(btype.syntaxOutput());
        sb.append(first.syntaxOutput());
        if (commas != null && constDefs != null && commas.size() == constDefs.size()) {
            int len = commas.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.commas.get(i).syntaxOutput());
                sb.append(this.constDefs.get(i).syntaxOutput());
            }
        }
        sb.append(this.semicn.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
