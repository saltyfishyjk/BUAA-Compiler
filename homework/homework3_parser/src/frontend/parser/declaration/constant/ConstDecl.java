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

    public ConstDecl(Token constTk,
                     BType btype,
                     ConstDef first,
                     ArrayList<Token> commas,
                     ArrayList<ConstDef> constDefs) {
        this.constTk = constTk;
        this.btype = btype;
        this.first = first;
        this.commas = commas;
        this.constDefs = constDefs;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(constTk);
        sb.append(btype);
        sb.append(first);
        if (commas != null && constDefs != null && commas.size() == constDefs.size()) {
            int len = commas.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.commas.get(i).syntaxOutput());
                sb.append(this.constDefs.get(i).syntaxOutput());
            }
        }
        return sb.toString();
    }
}