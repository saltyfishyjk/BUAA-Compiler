package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.parser.terminal.FormatString;
import frontend.parser.expression.Exp;

import java.util.ArrayList;

/**
 * <stmt> -> 'prinft' '(' <FormatString> {',' <Exp>} ')' ';'
 */
public class StmtPrint implements StmtEle {
    private Token printf; // 'printf'
    private Token leftParent; // '('
    private FormatString formatString;
    private ArrayList<Token> commmas; // ',' MAY exist
    private ArrayList<Exp> exps;
    private Token rightParent; // ')'
    private Token semicn; // ';'

    public StmtPrint(Token printf,
                     Token leftParent,
                     FormatString formatString,
                     ArrayList<Token> commas,
                     ArrayList<Exp> exps,
                     Token rightParent,
                     Token semicn) {
        this.printf = printf;
        this.leftParent = leftParent;
        this.formatString = formatString;
        this.commmas = commas;
        this.exps = exps;
        this.rightParent = rightParent;
        this.semicn = semicn;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.printf.syntaxOutput());
        sb.append(this.leftParent.syntaxOutput());
        sb.append(this.formatString.syntaxOutput());
        if (this.commmas != null && this.exps != null &&
            this.commmas.size() == this.exps.size()) {
            int len = this.commmas.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.commmas.get(i).syntaxOutput());
                sb.append(this.exps.get(i).syntaxOutput());
            }
        }
        sb.append(this.rightParent.syntaxOutput());
        sb.append(this.semicn.syntaxOutput());
        return sb.toString();
    }

    public FormatString getFormatString() {
        return this.formatString;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }
}
