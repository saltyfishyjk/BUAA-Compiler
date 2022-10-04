package frontend.parser.function;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.SyntaxNode;
import frontend.parser.declaration.BType;
import frontend.parser.expression.ConstExp;

import java.util.ArrayList;

public class FuncFParam implements SyntaxNode {
    private final String name = "<FuncFParam>";
    private BType btype;
    private Ident ident;
    private Token leftBrackFirst = null; // '[' MAY exist
    private Token rightBrackFirst = null; // ']' MAY exist
    private ArrayList<Token> leftBracks = null; // '[' MAY exist
    private ArrayList<ConstExp> constExps = null; // MAY exist
    private ArrayList<Token> rightBracks = null; // ']' MAY exist

    public FuncFParam(BType btype,
                      Ident ident) {
        this.btype = btype;
        this.ident = ident;
    }

    public FuncFParam(BType btype,
                      Ident ident,
                      Token leftBrackFirst,
                      Token rightBrackFirst) {
        this(btype, ident);
        this.leftBrackFirst = leftBrackFirst;
        this.rightBrackFirst = rightBrackFirst;
    }

    public FuncFParam(BType btype,
                      Ident ident,
                      Token leftBrackFirst,
                      Token rightBrackFirst,
                      ArrayList<Token> leftBracks,
                      ArrayList<ConstExp> constExps,
                      ArrayList<Token> rightBracks) {
        this(btype, ident, leftBrackFirst, rightBrackFirst);
        this.leftBracks = leftBracks;
        this.constExps = constExps;
        this.rightBracks = rightBracks;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.btype.syntaxOutput());
        sb.append(this.ident.syntaxOutput());
        if (this.leftBrackFirst != null && this.rightBrackFirst != null) {
            sb.append(this.leftBrackFirst.syntaxOutput());
            sb.append(this.rightBrackFirst.syntaxOutput());
            if (this.leftBracks != null && this.constExps != null && this.rightBracks != null &&
                this.leftBracks.size() == this.constExps.size() &&
                this.constExps.size() == this.rightBracks.size()) {
                int len = this.leftBracks.size();
                for (int i = 0; i < len; i++) {
                    sb.append(this.leftBracks.get(i).syntaxOutput());
                    sb.append(this.constExps.get(i).syntaxOutput());
                    sb.append(this.rightBracks.get(i).syntaxOutput());
                }
            }
        }
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
