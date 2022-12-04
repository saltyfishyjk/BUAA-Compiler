package frontend.parser.declaration.variable.initval;

import frontend.lexer.Token;

import java.util.ArrayList;

public class InitVals implements InitValEle {
    private Token leftBrace;
    private InitVal first; // may not exist
    private ArrayList<Token> commas;
    private ArrayList<InitVal> initVals;
    private Token rightBrace;

    public InitVals(Token leftBrace,
                    InitVal first,
                    ArrayList<Token> commas,
                    ArrayList<InitVal> initVals,
                    Token rightBrace) {
        this.leftBrace = leftBrace;
        this.first = first;
        this.commas = commas;
        this.initVals = initVals;
        this.rightBrace = rightBrace;
    }

    public ArrayList<InitVal> getAllInitVals() {
        ArrayList<InitVal> ret = new ArrayList<>();
        ret.add(this.first);
        if (this.initVals != null && this.initVals.size() > 0) {
            ret.addAll(this.initVals);
        }
        return ret;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.leftBrace.syntaxOutput());
        if (first != null) {
            sb.append(this.first.syntaxOutput());
            if (this.commas != null && this.initVals != null &&
                this.commas.size() == this.initVals.size()) {
                int len = this.commas.size();
                for (int i = 0; i < len; i++) {
                    sb.append(this.commas.get(i).syntaxOutput());
                    sb.append(this.initVals.get(i).syntaxOutput());
                }
            }
        }
        sb.append(this.rightBrace.syntaxOutput());
        return sb.toString();
    }
}
